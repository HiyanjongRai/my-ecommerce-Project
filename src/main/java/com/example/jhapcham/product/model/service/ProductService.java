package com.example.jhapcham.product.model.service;


import com.example.jhapcham.product.model.Product;
import com.example.jhapcham.product.model.dto.ProductDto;
import com.example.jhapcham.product.model.repository.ProductRepository;
import com.example.jhapcham.productLike.ProductLikeRepository;
import com.example.jhapcham.user.model.Role;
import com.example.jhapcham.user.model.User;
import com.example.jhapcham.user.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {

    @Autowired
    private ProductLikeRepository productLikeRepository;

    private final String uploadDir = "product-images"; // folder in local device

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(ProductDto dto, Long sellerId) throws Exception {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new Exception("Seller not found"));

        if (!seller.getRole().equals(Role.SELLER)) {
            throw new Exception("Only sellers can add products");
        }

        String fileName = null;
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            fileName = saveImage(dto.getImage());
        }

        Product product = Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .category(dto.getCategory())
                .sellerId(sellerId)
                .imagePath(fileName)
                .build();

        return productRepository.save(product);
    }

    public Product updateProduct(Long productId, ProductDto dto, Long sellerId) throws Exception {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        if (!product.getSellerId().equals(sellerId)) {
            throw new Exception("You can only update your own products");
        }

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setCategory(dto.getCategory());

        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            String fileName = saveImage(dto.getImage());
            product.setImagePath(fileName);
        }

        return productRepository.save(product);
    }

    public List<ProductDto> searchProducts(String keyword) {
        List<Product> products = productRepository.searchProducts(keyword);
        return products.stream()
                .map(this::toProductDto)
                .collect(Collectors.toList());
    }

    public void deleteProduct(Long productId, Long userId) throws Exception {
        // Find user (can be admin or seller)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception("User not found"));

        // Find product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new Exception("Product not found"));

        // ✅ Allow Admin to delete any product
        if (user.getRole().equals(Role.ADMIN)) {
            productRepository.deleteById(productId);
            return;
        }

        // ✅ Allow Seller to delete only their own product
        if (user.getRole().equals(Role.SELLER)) {
            if (!product.getSellerId().equals(user.getId())) {
                throw new Exception("You can delete only your own products");
            }
            productRepository.deleteById(productId);
            return;
        }

        throw new Exception("You are not authorized to delete products");
    }


    public List<Product> getProductsBySeller(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public ProductDto toProductDto(Product product) {
        int likeCount = productLikeRepository.countByProduct(product);

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .category(product.getCategory())
                .image(null) // Set to null or remove if not needed for GET
                .totalLikes(likeCount)
                .build();
    }

    // Save uploaded image to local directory
    private String saveImage(MultipartFile image) throws IOException {
        Files.createDirectories(Paths.get(uploadDir));

        String originalFilename = StringUtils.cleanPath(image.getOriginalFilename());
        String fileName = System.currentTimeMillis() + "_" + originalFilename;
        Path filePath = Paths.get(uploadDir, fileName);

        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName; // store only file name in DB
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
}