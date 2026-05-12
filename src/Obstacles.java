/////////////////////// OBSTACLES PROBLEM ###############################

//package com.example.employee.demo;

/*
We are writing software to collect and manage data on how fast racers can complete
obstacle courses.
 An obstacle course is a series of difficult physical challenges (like walls, hurdles,
 and ponds) that a racer must go through.

Each course consists of multiple obstacles. The software stores how long it takes for
 racers to
 finish each obstacle, and provides useful statistics based on those times.

Definitions:
* A "run" is a particular attempt to complete an entire obstacle course
* A "run collection" is a group of runs on a particular course by the user.
* An "obstacle" is a portion of a course. We track how long it takes to finish each
portion of the course

For example, here are some times for an obstacle course with four obstacles:

 Obstacles:    O1  O2  O3  O4
    Run 1:      3   4   5   6    (total: 18 seconds)
    Run 2:      4   4   4   5    (total: 17 seconds)
    Run 3:      4   5   4   6    (total: 19 seconds)
    Run 4:      5   5   3        (13 seconds, but run is incomplete)
    personal best -> minimum time(complete , answer -> run2 not run4 )
    o1->3, o2->4, o3->3, o4-> 5 = 15 (best of best)


All of these runs for one obstacle course (including the incomplete run) make up a run collection.

To begin with, we present you with two tasks:
1-1) Read through and understand the code below. Please take as much time as necessary, and feel free to
 run the code.
1-2) The test for RunCollection is not passing due to a bug in the code. Make the necessary changes to
 RunCollection to fix the bug.

2) We would like to implement a new function in RunCollection called "bestOfBests". This is
 a measure of how
fast a run could be if everything went perfectly, and is determined by taking the fastest
times for each
 obstacle across all runs (even incomplete ones) and summing them. In the run collection
 above,
  the times 3, 4, 3, and 5 combine to make a best of bests run of 15 seconds.

Implement this function, and add a test to verify that it works.

2-2) What other tests would you implement?

3) We would like to implement a new function "chanceOfPersonalBest". This
   takes in an in-progress Run object, and determines the chance that this run
   will be a new personal best, or match the current personal best.
   This assumes that, for every obstacle after the obstacles already completed
   in the run, the time to complete that obstacle is uniformly chosen from times
   to complete that obstacle in all other runs.

   We want to do this using simulation. To do this, write a method in
   RunCollection that takes in an in-progress run and does the
   following:
   * Over 10,000 trials:
     * For each incomplete segment in the current run:
       * Select a time randomly from previous runs (including incomplete runs)
     * Check if the run, including randomly collected times, is better than or matching
       the current personal best
   * Return the chance, over those trials, that the run was a personal best.

   The solution should consistently return a solution within .02 of the correct
   solutions. For example, if there is a .5 (50%) chance of a personal best, the
   function should return a value between .48 and .52.
*/

import java.util.*;

class Course {
    /* Data about a particular course. */
    public String title;  // The name of the obstacle course
    public int obstacleCount;  // The number of obstacles in the course

    public Course(String courseTitle, int obstacles) {
        title = courseTitle;
        obstacleCount = obstacles;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Course)) { return false; }
        Course c = (Course) o;
//        return c.title.equals(this.title) && c.obstacleCount == this.obstacleCount;
        return Objects.equals(title, c.title) && obstacleCount == c.obstacleCount;
    }

    @Override
    public int hashCode() {
//        return (title == null ? 0 : title.hashCode()) * obstacleCount;
        return Objects.hash(title, obstacleCount);
    }
}

class Run {
    /* Data and methods about a single run of the obstacle course */
    public Course course; // The Course object this run is for
    public boolean complete; // true if the run is a full run of the course
    // false if the run is in progress or was aborted
    public List<Integer> obstacleTimes; // The times it took to complete each obstacle

    public Run(Course runCourse) {
        course = runCourse;
        complete = false;
        obstacleTimes = new ArrayList<>();
    }

    public void addObstacleTime(int obstacleTime) {
        // When an obstacle is completed, add the time to the current run.
        // Params:
        //   obstacleTime: the time in seconds it took to complete the obstacle
        if(complete) {
            throw new IllegalStateException("Cannot add obstacle to complete run");
        }
        obstacleTimes.add(obstacleTime);
        if(obstacleTimes.size() == course.obstacleCount) {
            complete = true;
        }
    }

    public int getRunTime() {
        // Returns the total time this run has taken.
        // If the run is not complete, it returns the time taken so far.
        return obstacleTimes.stream().mapToInt(Integer::intValue).sum();
    }
}

class RunCollection {
    public Course course; // the Course this RunCollection is for
    public List<Run> runs;  // the Run objects for this particular course

    public RunCollection(Course collectionCourse) {
        course = collectionCourse;
        runs = new ArrayList<>();
    }

    public int getNumRuns() {
        // Returns the number of runs in this collection
        return runs.size();
    }

    public void addRun(Run run) {
        // Adds a run to this collection
        if(!run.course.equals(course)) {
            throw new IllegalArgumentException("run's Course is not the same as the RunCollection's");
        }
        runs.add(run);
    }

    public int personalBest() {
        // Returns the best finish time achieved in this RunCollection
        return runs.stream().filter(x -> x.complete).mapToInt(x -> x.getRunTime()).min().orElse(Integer.MAX_VALUE);
    }


//  this method used to find probability

    public double chanceOfPersonalBest(Run inProgress) {
//        step 1 variabe initialization
        int trials = 10_000;
        Random random = new Random();
        int best = personalBest();
        int completed = inProgress.obstacleTimes.size(); // obstacles already completed in current run
        int successes = 0; // how many times we beat the target
        List<List<Integer>> samples = new ArrayList<>(); // storage of past data for every obstacles

//        step 2 creating space for sampling
        for (int i = 0; i < course.obstacleCount; i++) {
            samples.add(new ArrayList<>());
        }
//        step 3 full sampling
        for (Run run : runs) {
            for (int i = 0; i < run.obstacleTimes.size(); i++) {
                samples.get(i).add(run.obstacleTimes.get(i));
            }
        }
//step 4 main logic
        for (int t = 0; t < trials; t++) {
            int time = inProgress.getRunTime();
            for (int i = completed; i < course.obstacleCount; i++) {
                List<Integer> list = samples.get(i);
                time += list.get(random.nextInt(list.size()));
            }
            if (time <= best) {
                successes++;
            }
        }
//        return the result
        System.out.println("Probability : " + successes / (double) trials);
        return successes / (double) trials;
    }
//this method is used to find best of every obstacle
    public int bestOfBests(){
        int total = 0;

        for (int i = 0; i < course.obstacleCount; i++) {
            int min = Integer.MAX_VALUE;
//      System.out.println(min);// testing
            for(Run r: runs){
                if(r.obstacleTimes.size() > i){
                    min = Math.min(min, r.obstacleTimes.get(i));
                }
            }
            if(min == Integer.MAX_VALUE){ //quazi_change4_replace 'best' with 'min'
                throw new IllegalArgumentException("No record for obstacle" + i);
            }

            total += min;
        }
        System.out.println("Total : " + total);
        return total;
    }

}

public class Obstacles {
    public static void main(String[] argv) {
        testRun();
    System.out.println("Test Run Success");
        testRunCollection();
        System.out.println("Test Run Collection Success");
        testBestOfBest();
        System.out.println("Test Best Of Best Success");
        testChanceOfPersonalBest();
        System.out.println("Test Chance of Personal Best Success");
        System.out.println("All test case passed");
    }

    // This is not a complete test suite, but tests some basic functionality of the above code, and
    // shows some examples of using the code.
    public static void testRun() {
        System.out.println("Running testRun");
        Course testCourse = new Course("Test course", 2);
        Run testRun = new Run(testCourse);
        testRun.addObstacleTime(3);
        assert !testRun.complete : "Test run should not be complete";
        testRun.addObstacleTime(5);
        assert testRun.complete : "Test run should be complete";
        assert testRun.obstacleTimes.equals(new ArrayList<Integer>(List.of(3, 5))) :
                "obstacleTimes should be [3, 5], was " + testRun.obstacleTimes;
        assert testRun.getRunTime() == 8 : "getRunTime should return 8, returned " + testRun.getRunTime();
        try {
            testRun.addObstacleTime(4);
            assert false : "adding obstacle time to complete run should throw";
        } catch(IllegalStateException e) {
            // expected
        }
    }


    public static RunCollection makeRunCollection(Course course, int[][] obstacleData) {
        // Create a new RunCollection for test purposes.
        // Params:
        //   course: the Course object this RunCollection is for
        //   obstacleData: an int[][]. Each int[] represents obstacle times for a single
        //                 run of the course.
        RunCollection runCollection = new RunCollection(course);
        for(int[] runData : obstacleData) {
            Run run = new Run(course);
            for(int obstacleTime : runData) {
                run.addObstacleTime(obstacleTime);
            }
            runCollection.addRun(run);
        }
        return runCollection;
    }

    public static void testBestOfBest() {
        // Tests basic RunCollection functionality

        //    Obstacles: O1  O2  O3  O4
        //    Run 1:      3   4   5   6    (total: 18 seconds)
        //    Run 2:      4   4   4   5    (total: 17 seconds)
        //    Run 3:      4   5   4   6    (total: 19 seconds)
        //    Run 4:      5   5   3        (13 seconds, but run is incomplete)
        System.out.println("Running testRunCollection");
        int[][] obstacleData = new int[][] {{3, 4, 5, 6},
                {4, 4, 4, 5},
                {4, 5, 4, 6},
                {5, 5, 3}};
        Course testCourse = new Course("Test course", 4);
        RunCollection runCollection = makeRunCollection(testCourse, obstacleData);

        runCollection.bestOfBests();

        assert runCollection.bestOfBests() == 15 :
                "personalBest should be 15, was " + runCollection.bestOfBests();
    }

    public static void testRunCollection() {
        // Tests basic RunCollection functionality

        //    Obstacles: O1  O2  O3  O4
        //    Run 1:      3   4   5   6    (total: 18 seconds)
        //    Run 2:      4   4   4   5    (total: 17 seconds)
        //    Run 3:      4   5   4   6    (total: 19 seconds)
        //    Run 4:      5   5   3        (13 seconds, but run is incomplete)
        System.out.println("Running testRunCollection");
        int[][] obstacleData = new int[][] {{3, 4, 5, 6},
                {4, 4, 4, 5},
                {4, 5, 4, 6},
                {5, 5, 3}};
        Course testCourse = new Course("Test course", 4);
        RunCollection runCollection = makeRunCollection(testCourse, obstacleData);

        int numRuns = obstacleData.length;
        assert runCollection.getNumRuns() == numRuns : "number of runs should be " + numRuns + ", was " + runCollection.getNumRuns();
        assert runCollection.personalBest() == 17 :
                "personalBest should be 17, was " + runCollection.personalBest();
    }

    public static void testChanceOfPersonalBest() {
        System.out.println("Running testChanceOfPersonalBest");
        // Test 1
        int[][] obstacleData = new int[][] {{3, 3, 2}, // 8
                {3, 3, 3}}; // 9
        Course testCourse = new Course("Test Course", 3);
        RunCollection runCollection = makeRunCollection(testCourse, obstacleData);
        Run testRun = new Run(testCourse);
        testRun.addObstacleTime(3);
        testRun.addObstacleTime(3);
        double chance = runCollection.chanceOfPersonalBest(testRun);
        // The current run has 2 obstacles in it, {3, 3, ?}
        // chance_of_personal_best will run 10,000 trials with to fill in the
        // remaining obstacles, randomly selecting one of 2 or 3 (the times for
        // the third obstacle)
        // The chance of the run being 8 seconds or less is 1/2
        assert .48 <= chance && chance <= .52 : "chance should be between .48 and .52, was " + chance;

        // Test 2
        obstacleData = new int[][] {{3, 3, 2, 3},
                {3, 3, 3, 2},
                {5, 5, 2}};
        testCourse = new Course("Test Course", 4);
        runCollection = makeRunCollection(testCourse, obstacleData);
        testRun = new Run(testCourse);
        testRun.addObstacleTime(3);
        testRun.addObstacleTime(3);
        chance = runCollection.chanceOfPersonalBest(testRun);
        // The current run has 2 obstacles in it, {3, 3, ?, ?}
        // chance_of_personal_best will run 10,000 trials with to fill in the
        // remaining two obstacles, randomly selecting one of:
        // {2, 3, 2} for obstacle 3 (includes the incomplete run)
        // {3, 2}    for obstacle 4
        // The chance of the run being 11 seconds or less is 5/6 ~= .83333
        assert .81333 <= chance && chance <= .85333 : "chance should be between .81333 and .85333, was " + chance;

        // Test 3
        obstacleData = new int[][] {{32, 37},
                {31, 29, 34, 25, 25, 39},
                {25, 34, 38, 24, 26, 39, 33},
                {39, 21, 39, 34, 39, 29, 31, 22, 28, 20},
                {23, 22, 35, 33, 36, 21, 29, 37, 24, 34},
                {28, 34, 28, 22, 40, 28, 31, 33, 25, 20},
                {20, 38, 40, 28, 34, 22},
                {36, 39, 20, 32, 38, 24, 22},
                {40, 20, 21, 37, 32, 30, 40, 25, 37, 30},
                {21, 35, 30, 37, 32, 40, 26, 29, 29}};
        testCourse = new Course("Test Course", 10);
        runCollection = makeRunCollection(testCourse, obstacleData);
        testRun = new Run(testCourse);
        testRun.addObstacleTime(19);
        testRun.addObstacleTime(19);
        testRun.addObstacleTime(19);
        chance = runCollection.chanceOfPersonalBest(testRun);
        assert .92813 <= chance && chance <= .96813 : "chance should be between .92813 and .96813, was " + chance;
    }
}
