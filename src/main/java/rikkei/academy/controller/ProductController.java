package rikkei.academy.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rikkei.academy.model.Product;
import rikkei.academy.model.ProductForm;
import rikkei.academy.service.IProductService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping({"/", "/product"})
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String index(Model model) {
        List<Product> productList = productService.findAll();
        model.addAttribute("products", productList);
        return "index";
    }

    @GetMapping("/create")
    public ModelAndView showCreatForm() {
        ModelAndView mnv = new ModelAndView("create");
        mnv.addObject("productForm", new ProductForm());
        return mnv;
    }

    @Value("${file-upload}")
    private String fileUpload;

    @PostMapping("/save")
    public String saveProduct(ProductForm productForm, RedirectAttributes redirectAttributes) {
        MultipartFile multipartFile = productForm.getImage();
        String fileName = multipartFile.getOriginalFilename();
        try {
            FileCopyUtils.copy(productForm.getImage().getBytes(), new File(fileUpload + fileName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Product product = new Product(productForm.getId(), productForm.getName(), productForm.getDescription(), fileName);
        productService.save(product);
        redirectAttributes.addFlashAttribute("message", "Create product successfully!");
        return "redirect:/create";
    }
}
