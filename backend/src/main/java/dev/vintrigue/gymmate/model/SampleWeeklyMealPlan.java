package dev.vintrigue.gymmate.model;

import java.util.*;

public class SampleWeeklyMealPlan {
    public static WeeklyMealPlan createSampleWeeklyMealPlan() {
        WeeklyMealPlan weeklyMealPlan = new WeeklyMealPlan();
        weeklyMealPlan.setId("sample-weekly-meal-plan");
        weeklyMealPlan.setUserId("sample-user");
        
        Map<String, List<MealDetails>> dailyMeals = new HashMap<>();
        
        // Monday - Upper Body Day
        dailyMeals.put("Monday", Arrays.asList(
            new MealDetails("m1", "Protein Oatmeal", "Oatmeal with protein powder, banana, and nuts", 450, 10),
            new MealDetails("m2", "Grilled Chicken Salad", "Grilled chicken breast with mixed greens and olive oil dressing", 550, 20),
            new MealDetails("m3", "Protein Smoothie", "Whey protein with almond milk and berries", 300, 5),
            new MealDetails("m4", "Salmon with Sweet Potato", "Baked salmon with roasted sweet potato and steamed vegetables", 650, 30)
        ));

        // Tuesday - Lower Body Day
        dailyMeals.put("Tuesday", Arrays.asList(
            new MealDetails("m5", "Greek Yogurt Parfait", "Greek yogurt with granola and honey", 400, 5),
            new MealDetails("m6", "Turkey Wrap", "Whole grain wrap with turkey, avocado, and vegetables", 500, 15),
            new MealDetails("m7", "Protein Bar", "High protein bar with nuts", 250, 0),
            new MealDetails("m8", "Lean Beef Stir Fry", "Lean beef with brown rice and stir-fried vegetables", 700, 25)
        ));

        // Wednesday - Cardio Day
        dailyMeals.put("Wednesday", Arrays.asList(
            new MealDetails("m9", "Fruit and Protein Smoothie", "Mixed berries with protein powder and almond milk", 350, 5),
            new MealDetails("m10", "Quinoa Bowl", "Quinoa with roasted vegetables and chickpeas", 450, 20),
            new MealDetails("m11", "Trail Mix", "Mixed nuts and dried fruits", 300, 0),
            new MealDetails("m12", "Grilled Fish with Vegetables", "Grilled fish with steamed vegetables and quinoa", 550, 25)
        ));

        // Thursday - Upper Body Day
        dailyMeals.put("Thursday", Arrays.asList(
            new MealDetails("m13", "Protein Pancakes", "Protein pancakes with maple syrup and berries", 500, 15),
            new MealDetails("m14", "Tuna Salad", "Tuna salad with whole grain crackers", 450, 10),
            new MealDetails("m15", "Protein Shake", "Whey protein with water and banana", 300, 5),
            new MealDetails("m16", "Chicken Stir Fry", "Chicken with brown rice and stir-fried vegetables", 600, 25)
        ));

        // Friday - Lower Body Day
        dailyMeals.put("Friday", Arrays.asList(
            new MealDetails("m17", "Avocado Toast", "Whole grain toast with avocado and eggs", 450, 10),
            new MealDetails("m18", "Protein Pasta", "Whole grain pasta with lean ground turkey and tomato sauce", 550, 20),
            new MealDetails("m19", "Greek Yogurt with Berries", "Greek yogurt with fresh berries and honey", 300, 5),
            new MealDetails("m20", "Grilled Steak with Vegetables", "Grilled steak with roasted vegetables and sweet potato", 700, 30)
        ));

        // Saturday - Cardio Day
        dailyMeals.put("Saturday", Arrays.asList(
            new MealDetails("m21", "Smoothie Bowl", "Acai bowl with granola and fruits", 400, 10),
            new MealDetails("m22", "Mediterranean Salad", "Mixed greens with feta, olives, and grilled chicken", 500, 15),
            new MealDetails("m23", "Protein Cookies", "Homemade protein cookies", 250, 5),
            new MealDetails("m24", "Baked Chicken with Rice", "Baked chicken breast with brown rice and vegetables", 600, 25)
        ));

        // Sunday - Rest Day
        dailyMeals.put("Sunday", Arrays.asList(
            new MealDetails("m25", "Overnight Oats", "Overnight oats with chia seeds and fruits", 400, 5),
            new MealDetails("m26", "Vegetable Soup", "Hearty vegetable soup with whole grain bread", 450, 20),
            new MealDetails("m27", "Fruit and Nut Mix", "Mixed fruits and nuts", 300, 0),
            new MealDetails("m28", "Baked Fish with Vegetables", "Baked fish with roasted vegetables and quinoa", 550, 25)
        ));

        weeklyMealPlan.setDailyMeals(dailyMeals);
        
        // Calculate total weekly calories and prep time
        int totalCalories = 0;
        int totalPrepTime = 0;
        
        for (List<MealDetails> meals : dailyMeals.values()) {
            for (MealDetails meal : meals) {
                totalCalories += meal.getCalories();
                totalPrepTime += meal.getPrepTime();
            }
        }
        
        weeklyMealPlan.setTotalWeeklyCalories(totalCalories);
        weeklyMealPlan.setTotalWeeklyPrepTime(totalPrepTime);
        
        return weeklyMealPlan;
    }
} 