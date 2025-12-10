package com.demo.sell_card_demo1.service;

import com.demo.sell_card_demo1.dto.*;
import com.demo.sell_card_demo1.entity.*;
import com.demo.sell_card_demo1.enums.CardStatus;
import com.demo.sell_card_demo1.exception.BadRequestException;
import com.demo.sell_card_demo1.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private BranchRepository branchRepository;
    @Autowired
    private ProductVariantsRepository productVariantsRepository;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private DiscountRepository discountRepository;
    @Autowired
    private S3Service s3Service;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;
    @Value("${aws.s3.region}")
    private String region;


    public ProductResponse convertToProductResponse(Product product) {
        if (product == null) return null;
        ProductResponse response = new ProductResponse();
        response.productId = product.getProductId();
        response.productName = product.getName();
        response.productDescription = product.getDescription();
        response.pictureURL = s3Service.getUrl(product.getPictureUrl());

        if (product.getBranch() != null) {
            Branch branchEntity = product.getBranch();
            response.branch = new BranchResponse(branchEntity.getBranchId(), branchEntity.getName());
        }
        response.productVariantResponses = productVariantsRepository.findByProduct_ProductId(product.getProductId()).stream()
                .map(this::convertToProductVariantResponse)
                .toList();
        return response;
    }
    public ProductResponse convertToProductResponseWithoutVariants(Product product) {
        if (product == null) return null;
        ProductResponse response = new ProductResponse();
        response.productId = product.getProductId();
        response.productName = product.getName();
        response.productDescription = product.getDescription();
        response.pictureURL = s3Service.getUrl(product.getPictureUrl());

        if (product.getBranch() != null) {
            Branch branchEntity = product.getBranch();
            response.branch = new BranchResponse(branchEntity.getBranchId(), branchEntity.getName());
        }
        return response;
    }

    public ProductVariantResponse convertToProductVariantResponse(ProductVariant productVariant) {
        if (productVariant == null) return null;
        ProductVariantResponse response = new ProductVariantResponse();
        response.id = productVariant.getVariantId();
        response.currency = productVariant.getCurrency();
        response.price = productVariant.getPrice();
        response.value = productVariant.getValue();
        return response;
    }

    /**
     * API cơ bản lấy tất cả (Dùng cho Admin quản lý danh sách)
     */
    public Page<ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::convertToProductResponse);
    }

    /**
     * API Nâng cao: Tìm kiếm và Lọc (Dùng cho trang chủ Public/Member)
     * Hỗ trợ tìm theo Tên, Nhà mạng, Khoảng giá.
     */
    public Page<ProductResponse> searchProductsPublic(String keyword, String branchName, Long minPrice, Long maxPrice, Pageable pageable) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(keyword)) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }

            if (StringUtils.hasText(branchName)) {
                predicates.add(cb.equal(root.get("branch").get("name"), branchName));
            }

            if (minPrice != null || maxPrice != null) {
                var variantJoin = root.join("variant");
                if (minPrice != null) {
                    predicates.add(cb.greaterThanOrEqualTo(variantJoin.get("price"), minPrice));
                }
                if (maxPrice != null) {
                    predicates.add(cb.lessThanOrEqualTo(variantJoin.get("price"), maxPrice));
                }
                query.distinct(true); // Tránh duplicate product nếu có nhiều variant thỏa mãn
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return productRepository.findAll(spec, pageable).map(this::convertToProductResponseWithoutVariants);
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Product not found with id: " + id));

        return convertToProductResponse(product);
    }

    public ProductResponse getProductByName(String name) {
        Product product = productRepository.getProductByName(name);
        if (product == null) throw new BadRequestException("Product not found with name: " + name);
        return convertToProductResponse(product);
    }

    public List<ProductVariantResponse> getAllProductVariants(Long productId) {
        return productVariantsRepository.findByProduct_ProductId(productId).stream()
                .map(this::convertToProductVariantResponse)
                .toList();
    }

    public ProductResponse createProduct(CreateProductRequest request) {
        if (productRepository.existsByName(request.getName())) {
            throw new BadRequestException("Product name already exists");
        }

        Discount discount = null;
        if(StringUtils.hasText(request.getDiscountCode())){
            discount = discountRepository.findByCode(request.getDiscountCode());
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        Branch branch = branchRepository.findBranchByName(request.getBranchName());
        if (branch == null) {
            Branch newBranch = new Branch();
            newBranch.setName(request.getBranchName());
            branchRepository.save(newBranch);
            product.setBranch(newBranch);
        }else{
            product.setBranch(branch);
        }
        product.setPictureUrl(request.getPictureUrl());

        if (discount != null) product.setDiscount(discount);

        Product savedProduct = productRepository.save(product);
        return convertToProductResponse(savedProduct);
    }

    public ProductResponse updateProduct(Long productId, UpdateProductRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        Optional<Product> existingProduct = productRepository.findByName(request.getName());
        if (existingProduct.isPresent() && !existingProduct.get().getProductId().equals(productId)) {
            throw new BadRequestException("Product name already exists");
        }

        Branch branch = branchRepository.findBranchByName(request.getBranchName());
        if (branch == null) throw new BadRequestException("Branch not found");

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setBranch(branch);
        product.setPictureUrl(request.getImageUrl());

        if(StringUtils.hasText(request.getDiscountCode())){
            Discount discount = discountRepository.findByCode(request.getDiscountCode());
            if (discount != null) product.setDiscount(discount);
        }
        if (request.getImageUrl() != null) {
            product.setPictureUrl(request.getImageUrl());
        }
        Product savedProduct = productRepository.save(product);
        return convertToProductResponse(savedProduct);
    }

    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new BadRequestException("Product not found with id: " + productId);
        }
        try {
            productRepository.deleteById(productId);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Cannot delete product because it has existing variants or stock transactions.");
        }
    }

    // ==================================================================================
    // 4. VARIANT & INVENTORY MANAGEMENT (QUẢN LÝ BIẾN THỂ & KHO)
    // ==================================================================================

    public ProductVariantResponse addProductVariant(Long productId, AddVariantRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));

        ProductVariant variant = new ProductVariant();
        variant.setPrice(request.getPrice());
        variant.setCurrency(request.getCurrency());
        variant.setValue(request.getValue());
        variant.setProduct(product);

        ProductVariant savedVariant = productVariantsRepository.save(variant);
        return convertToProductVariantResponse(savedVariant);
    }

    public ProductVariantResponse updateProductVariant(Long variantId, UpdateVariantRequest request) {
        ProductVariant variant = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new BadRequestException("Variant not found"));

        variant.setPrice(request.getPrice());
        variant.setCurrency(request.getCurrency());
        variant.setValue(request.getValue());

        ProductVariant savedVariant = productVariantsRepository.save(variant);
        return convertToProductVariantResponse(savedVariant);
    }

    /**
     * Nhập kho (Add Stock)
     * Thêm mã thẻ mới vào kho cho một biến thể cụ thể.
     */
    public void addStockToVariant(Long variantId, AddStockRequest request) {
        ProductVariant variant = productVariantsRepository.findById(variantId)
                .orElseThrow(() -> new BadRequestException("Variant not found"));

        List<Storage> newStock = new ArrayList<>();

        LocalDate activeDate = request.getActivationDate() != null ? request.getActivationDate() : LocalDate.now();

        for (String code : request.getActivationCodes()) {
            Storage storage = new Storage();
            storage.setVariant(variant);
            storage.setActivateCode(code);

            storage.setActivationDate(activeDate.atStartOfDay());

            if (request.getExpirationDate() != null) {
                storage.setExpirationDate(request.getExpirationDate().atTime(23, 59, 59));
            }
            // ------------------------------------------------

            storage.setStatus(CardStatus.UNUSED);
            newStock.add(storage);
        }
        storageRepository.saveAll(newStock);
    }

    /**
     * MỚI: Xem thống kê tồn kho (Inventory Status)
     * Giúp Admin biết loại thẻ nào còn bao nhiêu cái.
     */
    public List<InventoryStatusResponse> getInventoryStatus() {
        // Hàm countInventoryByVariant() phải được khai báo trong StorageRepository với câu Query Group By
        return storageRepository.countInventoryByVariant();
    }

    // ==================================================================================
    // 5. FILE UPLOAD (MỚI THÊM)
    // ==================================================================================

    /**
     * Upload ảnh sản phẩm vào thư mục local 'uploads/'
     * Trả về đường dẫn ảnh tương đối.
     */
    public ProductResponse uploadProductImage(Long productId, MultipartFile file ){
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BadRequestException("Product not found"));
        try {
            String extension = getFileExtension(file.getOriginalFilename());

            String s3Key = s3Service.uploadFile(
                    "products",
                    String.valueOf(productId),
                    file.getInputStream(),
                    file.getSize(),
                    extension
            );

            product.setPictureUrl(s3Key);
            productRepository.save(product);

            return convertToProductResponse(product);
        } catch (Exception e) {
            throw new BadRequestException("Could not store file: " + e.getMessage());
        }

    }
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1);
        }
        return "jpg";
    }
    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }
    public Branch deleteBranch(Long branchId) {
        Branch branch = branchRepository.findById(branchId)
                .orElseThrow(() -> new BadRequestException("Branch not found"));
        branchRepository.delete(branch);
        return branch;
    }
    public Branch createBranch(String branchName) {
        if (branchRepository.findBranchByName(branchName) != null) {
            throw new BadRequestException("Branch name already exists");
        }
        Branch branch = new Branch();
        branch.setName(branchName);
        branchRepository.save(branch);
        return branch;
    }
    public Page<ProductResponse> getProductsByBranch(Long branchId, Pageable pageable) {
        if (!branchRepository.existsById(branchId)) {
            throw new BadRequestException("Branch not found with id: " + branchId);
        }

        Page<Product> productPage = productRepository.findByBranch_BranchId(branchId, pageable);

        return productPage.map(this::convertToProductResponse);
    }
}