package com.example.fitgo;

import java.util.List;

public class ExerciseGroup {
    String groupName;
    List<Exercise> exercises;

    public ExerciseGroup(String groupName, List<Exercise> exercises) {
        this.groupName = groupName;
        this.exercises = exercises;
    }

    public String getGroupName() {
        return groupName;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }
}
