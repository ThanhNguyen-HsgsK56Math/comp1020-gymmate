package dev.vintrigue.gymmate.controller;

import dev.vintrigue.gymmate.model.DailyPlan;
import dev.vintrigue.gymmate.service.DailyPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dailyplans")
public class DailyPlanController {

    @Autowired
    private DailyPlanService dailyPlanService;

    @GetMapping
    public ResponseEntity<?> getDailyPlan(@RequestParam String userId) {
        try {
            DailyPlan plan = dailyPlanService.generatePlan(userId);
            return ResponseEntity.ok(plan);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body("Error: " + e.getMessage());
        }
    }
}