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
    public ResponseEntity<DailyPlan> getDailyPlan(@RequestParam String userId) { // This will generate the daily plan for the user
        DailyPlan plan = dailyPlanService.generatePlan(userId); 
        return ResponseEntity.ok(plan);
    }
}