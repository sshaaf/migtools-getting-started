package com.redhat.mta.examples.spring.framework.controller;

import com.redhat.mta.examples.spring.framework.model.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Spring MVC Controller using deprecated patterns.
 * 
 * Deprecated patterns for Spring Framework 6 migration:
 * - javax.servlet API usage (should be jakarta.servlet in Spring 6)
 * - Legacy @RequestMapping patterns
 * - Deprecated ModelAndView usage patterns
 * - Legacy parameter binding approaches
 * - Deprecated exception handling patterns
 */
@Controller
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Deprecated: Legacy @RequestMapping without specific HTTP method
     * Spring 6: Should use specific mapping annotations (@GetMapping, @PostMapping, etc.)
     */
    @RequestMapping("/list")
    public ModelAndView listUsers(HttpServletRequest request, HttpSession session) {
        // Deprecated: Direct HttpServletRequest and HttpSession usage
        String sortBy = request.getParameter("sort");
        Integer pageSize = session.getAttribute("pageSize") != null ? 
            (Integer) session.getAttribute("pageSize") : 10;
            
        List<User> users = userService.findAllUsers(sortBy, pageSize);
        
        // Deprecated: ModelAndView construction pattern
        ModelAndView mav = new ModelAndView("users/list");
        mav.addObject("users", users);
        mav.addObject("currentSort", sortBy);
        mav.addObject("pageSize", pageSize);
        return mav;
    }

    /**
     * Deprecated: Legacy form handling with ModelAndView
     * Spring 6: Should use more modern response patterns
     */
    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public ModelAndView showCreateForm(HttpServletRequest request) {
        // Deprecated: HttpServletRequest for simple parameter access
        String returnUrl = request.getParameter("returnUrl");
        
        ModelAndView mav = new ModelAndView("users/create");
        mav.addObject("user", new User());
        mav.addObject("departments", userService.getAllDepartments());
        mav.addObject("returnUrl", returnUrl);
        return mav;
    }

    /**
     * Deprecated: Legacy form submission handling
     * Spring 6: Should use proper validation and response patterns
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public String createUser(@Valid @ModelAttribute("user") User user,
                           BindingResult result,
                           HttpServletRequest request,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        
        // Deprecated: Manual validation error handling
        if (result.hasErrors()) {
            model.addAttribute("departments", userService.getAllDepartments());
            model.addAttribute("errors", result.getAllErrors());
            return "users/create";
        }
        
        // Deprecated: HttpServletRequest for parameter access
        String returnUrl = request.getParameter("returnUrl");
        String userAgent = request.getHeader("User-Agent");
        
        try {
            User savedUser = userService.createUser(user);
            
            // Deprecated: RedirectAttributes usage pattern
            redirectAttributes.addFlashAttribute("successMessage", 
                "User created successfully: " + savedUser.getEmail());
            redirectAttributes.addAttribute("userId", savedUser.getId());
            
            // Deprecated: Conditional redirect logic in controller
            if (returnUrl != null && !returnUrl.isEmpty()) {
                return "redirect:" + returnUrl;
            }
            return "redirect:/users/view/" + savedUser.getId();
            
        } catch (Exception e) {
            // Deprecated: Exception handling in controller
            model.addAttribute("errorMessage", "Failed to create user: " + e.getMessage());
            model.addAttribute("departments", userService.getAllDepartments());
            return "users/create";
        }
    }

    /**
     * Deprecated: Legacy path variable and request parameter mixing
     * Spring 6: Should use consistent parameter binding approaches
     */
    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public ModelAndView editUser(@PathVariable("id") Long userId,
                               @RequestParam(value = "tab", defaultValue = "general") String activeTab,
                               HttpServletRequest request,
                               HttpSession session) {
        
        // Deprecated: HttpSession for user preferences
        session.setAttribute("lastEditedUserId", userId);
        session.setAttribute("preferredTab", activeTab);
        
        // Deprecated: HttpServletRequest for complex parameter handling
        String referrer = request.getHeader("Referer");
        Map<String, String> requestParams = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values.length > 0) {
                requestParams.put(key, values[0]);
            }
        });
        
        User user = userService.findUserById(userId);
        if (user == null) {
            // Deprecated: Null check pattern in controller
            ModelAndView mav = new ModelAndView("error/404");
            mav.addObject("message", "User not found with ID: " + userId);
            return mav;
        }
        
        ModelAndView mav = new ModelAndView("users/edit");
        mav.addObject("user", user);
        mav.addObject("departments", userService.getAllDepartments());
        mav.addObject("activeTab", activeTab);
        mav.addObject("referrer", referrer);
        mav.addObject("requestParams", requestParams);
        return mav;
    }

    /**
     * Deprecated: Legacy AJAX endpoint with manual JSON handling
     * Spring 6: Should use proper REST patterns and response entities
     */
    @RequestMapping(value = "/api/search", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> searchUsers(@RequestParam("query") String query,
                                         @RequestParam(value = "page", defaultValue = "0") int page,
                                         @RequestParam(value = "size", defaultValue = "10") int size,
                                         HttpServletRequest request,
                                         HttpServletResponse response) {
        
        // Deprecated: Manual CORS handling
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        
        // Deprecated: Manual response construction
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<User> users = userService.searchUsers(query, page, size);
            long totalCount = userService.countUsersByQuery(query);
            
            result.put("success", true);
            result.put("data", users);
            result.put("totalCount", totalCount);
            result.put("currentPage", page);
            result.put("pageSize", size);
            result.put("totalPages", (totalCount + size - 1) / size);
            
            // Deprecated: Manual request logging
            String clientIp = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            // Log search request with deprecated logging approach
            
        } catch (Exception e) {
            // Deprecated: Exception handling in API endpoint
            result.put("success", false);
            result.put("error", e.getMessage());
            result.put("errorCode", "SEARCH_FAILED");
            
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        
        return result;
    }

    /**
     * Deprecated: Legacy file upload handling
     * Spring 6: Should use proper multipart handling patterns
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadUserData(@RequestParam("file") org.springframework.web.multipart.MultipartFile file,
                                @RequestParam(value = "format", defaultValue = "csv") String format,
                                HttpServletRequest request,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        
        // Deprecated: Manual file validation
        if (file.isEmpty()) {
            model.addAttribute("errorMessage", "Please select a file to upload");
            return "users/upload";
        }
        
        // Deprecated: Manual MIME type checking
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("text/csv") && !contentType.equals("application/vnd.ms-excel"))) {
            model.addAttribute("errorMessage", "Invalid file type. Please upload CSV or Excel file.");
            return "users/upload";
        }
        
        // Deprecated: Manual file size validation
        long maxSize = 5 * 1024 * 1024; // 5MB
        if (file.getSize() > maxSize) {
            model.addAttribute("errorMessage", "File size exceeds maximum limit of 5MB");
            return "users/upload";
        }
        
        try {
            // Deprecated: Direct file processing in controller
            byte[] fileContent = file.getBytes();
            String fileName = file.getOriginalFilename();
            
            int processedCount = userService.processUserDataFile(fileContent, format, fileName);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Successfully processed " + processedCount + " users from file: " + fileName);
            return "redirect:/users/list";
            
        } catch (Exception e) {
            // Deprecated: Exception handling pattern
            model.addAttribute("errorMessage", "Failed to process file: " + e.getMessage());
            return "users/upload";
        }
    }

    /**
     * Deprecated: Legacy @InitBinder usage
     * Spring 6: Should use proper validation and conversion patterns
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Deprecated: Custom property editor registration
        binder.registerCustomEditor(java.util.Date.class, 
            new org.springframework.beans.propertyeditors.CustomDateEditor(
                new java.text.SimpleDateFormat("yyyy-MM-dd"), false));
                
        // Deprecated: Manual field validation setup
        binder.setAllowedFields("firstName", "lastName", "email", "department", "active", "salary");
        binder.setDisallowedFields("id", "createdDate", "lastLoginDate");
        
        // Deprecated: Custom validator registration
        binder.addValidators(new LegacyUserValidator());
    }

    /**
     * Deprecated: Legacy exception handler in controller
     * Spring 6: Should use global exception handling
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException(UserNotFoundException e, HttpServletRequest request) {
        // Deprecated: Exception handling with HttpServletRequest
        String requestUrl = request.getRequestURL().toString();
        
        ModelAndView mav = new ModelAndView("error/user-not-found");
        mav.addObject("errorMessage", e.getMessage());
        mav.addObject("requestUrl", requestUrl);
        mav.addObject("timestamp", java.time.LocalDateTime.now());
        return mav;
    }

    /**
     * Inner class demonstrating deprecated validation patterns
     */
    private static class LegacyUserValidator implements org.springframework.validation.Validator {
        
        @Override
        public boolean supports(Class<?> clazz) {
            return User.class.equals(clazz);
        }
        
        @Override
        public void validate(Object target, org.springframework.validation.Errors errors) {
            User user = (User) target;
            
            // Deprecated: Manual validation logic in validator
            if (user.getEmail() != null && !user.getEmail().contains("@")) {
                errors.rejectValue("email", "invalid.email", "Invalid email format");
            }
            
            if (user.getFirstName() != null && user.getFirstName().length() < 2) {
                errors.rejectValue("firstName", "too.short", "First name must be at least 2 characters");
            }
            
            // Deprecated: Business logic in validator
            if (user.getSalary() != null && user.getSalary() < 0) {
                errors.rejectValue("salary", "negative.value", "Salary cannot be negative");
            }
        }
    }
}

// Supporting classes and exceptions
class UserService {
    public List<User> findAllUsers(String sortBy, Integer pageSize) { return null; }
    public List<Object> getAllDepartments() { return null; }
    public User createUser(User user) { return user; }
    public User findUserById(Long id) { return null; }
    public List<User> searchUsers(String query, int page, int size) { return null; }
    public long countUsersByQuery(String query) { return 0; }
    public int processUserDataFile(byte[] content, String format, String fileName) { return 0; }
}

class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) { super(message); }
}
