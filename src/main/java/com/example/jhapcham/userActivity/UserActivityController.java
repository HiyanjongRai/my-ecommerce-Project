//package com.example.jhapcham.userActivity;
//
//import com.example.jhapcham.activity.model.UserActivity;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import java.util.List;
//
//@RestController
//@RequestMapping("/activity")
//@RequiredArgsConstructor
//public class UserActivityController {
//
//    private final UserActivityService userActivityService;
//
//    @PostMapping("/log")
//    public ResponseEntity<?> logActivity(@RequestParam Long userId,
//                                         @RequestParam(required = false) Long productId,
//                                         @RequestParam String actionType,
//                                         @RequestParam(required = false) String searchKeyword) {
//        try {
//            userActivityService.logActivity(userId, productId, actionType, searchKeyword);
//            return ResponseEntity.ok("Activity logged successfully");
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }
//
//    @GetMapping("/user/{userId}")
//    public ResponseEntity<List<UserActivity>> getUserActivity(@PathVariable Long userId) {
//        return ResponseEntity.ok(userActivityService.getUserActivity(userId));
//    }
//}
