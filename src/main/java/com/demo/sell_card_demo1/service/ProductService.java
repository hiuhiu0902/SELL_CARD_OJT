package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.dto.*;
import com.demo.sell_card_demo1.entity.*;
import com.demo.sell_card_demo1.enums.CardStatus;
import com.demo.sell_card_demo1.exception.BadRequestException;
import com.demo.sell_card_demo1.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
public class ProductService {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    BranchRepository branchRepository;
    @Autowired
    ProductVariantsRepository  productVariantsRepository;
    @Autowired
    StorageRepository storageRepository;
    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    DiscountRepository discountRepository;
    public Page<ProductResponse> getAllProducts(Pageable pageable){
        Page<Product> productPage = productRepository.findAll(pageable);
        return productPage.map(this::convertToProductResponse);
    }
    public ProductResponse convertToProductResponse(Product product){
        ProductResponse response = new ProductResponse();
        response.productId = product.getProductId();
        response.productName = product.getName();
        response.productDescription = product.getDescription();
        response.pictureURL = product.getPictureUrl();
        if(product.getBranch() != null){
            Branch branchEntity = product.getBranch();
            response.branch = new BranchResponse(branchEntity.getBranchId(), branchEntity.getName());
        }
        if(product.getVariant() != null && !product.getVariant().isEmpty()){
            response.productVariantResponses = product.getVariant().stream()
                    .map(this::convertToProductVariantResponse)
                    .toList();
        }
        return response;
    }
    public ProductVariantResponse convertToProductVariantResponse(ProductVariant productVariant){
        ProductVariantResponse response = new ProductVariantResponse();
        response.id = productVariant.getVariantId();
        response.currency = productVariant.getCurrency();
        response.price = productVariant.getPrice();
        response.value = productVariant.getValue();
        return response;
    }
    public Product getProductByName(String name){
        return productRepository.getProductByName(name);
    }
    public Product getProductById(Long id){
        return productRepository.findById(id).orElse(null);
    }
    public Product addProduct(CreateProductRequest request){
        User user = authenticationService.getCurrentUser();
        Product product = new Product();
        System.out.println("Add lan 1");
        System.out.println(user.getRole());
        Branch branch = branchRepository.findBranchByName(request.getBranchName());
        if(branch==null){
            throw new BadRequestException("Branch not found");
        }
        Discount discount = discountRepository.findByCode(request.getDiscountCode());
        if(discount == null){
            throw new BadRequestException("Discount not found");
        }
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setBranch(branch);
            product.setPictureUrl(request.getPictureUrl());
            product.setDiscount(discount);
            productRepository.save(product) ;
        return product;
    }
    public Product createProduct(CreateProductRequest request){
        if(productRepository.existsByName(request.getName())){
            throw new BadRequestException("Product name already exists");
        }
        Branch branch = branchRepository.findBranchByName(request.getBranchName());
        if(branch==null){
            throw new BadRequestException("Branch not found");
        }
        Discount discount = discountRepository.findByCode(request.getDiscountCode());
        if(discount == null){
            throw new BadRequestException("Discount not found");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBranch(branch);
        product.setPictureUrl(request.getPictureUrl());
        product.setDiscount(discount);

        return productRepository.save(product);
    }
    public ProductVariant addProductVariant(Long productId, AddVariantRequest request){
        Product product = productRepository.findById(productId).orElse(null);

        ProductVariant variant = new ProductVariant();
        variant.setPrice(request.getPrice());
        variant.setCurrency(request.getCurrency());
        variant.setValue(request.getValue());
        variant.setProduct(product);

        return productVariantsRepository.save(variant);
    }

    public List<Storage> addStockToVariant(Long variantId, AddStockRequest request){
        ProductVariant variant = productVariantsRepository.findById(variantId).orElse(null);

        List<Storage> newStock = new ArrayList<>();
        for(String code : request.getActivationCodes()){
            Storage storage = new Storage();
            storage.setVariant(variant);
            storage.setActivateCode(code);
            storage.setActivationDate(request.getActivationDate());
            storage.setExpirationDate(request.getExpirationDate());
            storage.setStatus(CardStatus.UNUSED);
            newStock.add(storage);
        }
        return storageRepository.saveAll(newStock);
    }
    public List<ProductVariantResponse> getAllProductVariants(Long productId){
        List<ProductVariant> variants = productVariantsRepository.findByProductId(productId);
        List<ProductVariantResponse> responses = variants.stream()
                .map(this::convertToProductVariantResponse)
                .toList();
        return responses;
    }
}
