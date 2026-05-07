//?????????????? Membership GYm #######################################

//package com.example.employee.demo;



/*
Karat Live Coding Practice – Gym Membership System

TASK OVERVIEW:

1-1: Read and understand the code below.
1-2: Unit tests are failing due to a bug. Fix the Membership class.

2-1: Implement addWorkout
    - Add workout only if member exists
    - If member does not exist, ignore

2-2: Implement getAverageWorkoutDurations
    - Return average workout duration per member
    - Key: memberId
    - Value: average duration
    - duration = endTime - startTime
*/

//import org.hibernate.jdbc.Work;

import java.util.*;

enum MembershipStatus {
    BRONZE, SILVER, GOLD
}

class Member {
    int memberId;
    String name;
    MembershipStatus membershipStatus;

    public Member(int memberId, String name, MembershipStatus status) {
        this.memberId = memberId;
        this.name = name;
        this.membershipStatus = status;
    }
}

class Workout {
    int workoutId;
    int startTime;
    int endTime;

    public Workout(int workoutId, int startTime, int endTime) {
        this.workoutId = workoutId;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getDuration() {
        return endTime - startTime;
    }
}

class MembershipStatistics {
    int totalMembers;
    int totalPaidMembers;
    double conversionRate;

    public MembershipStatistics(int totalMembers, int totalPaidMembers, double conversionRate) {
        this.totalMembers = totalMembers;
        this.totalPaidMembers = totalPaidMembers;
        this.conversionRate = conversionRate;
    }
}

class Membership {

    List<Member> members = new ArrayList<>();
    Map<Integer, List<Workout>> workoutsByMember = new HashMap<>();

    public void addMember(Member member) {
        members.add(member);
    }

    // BUG PRESENT HERE (do not fix now)
    public MembershipStatistics getMembershipStatistics() {
        int totalMembers = members.size();
        int paid = 0;

        for (Member m : members) {
            if (m.membershipStatus == MembershipStatus.GOLD ||
                    m.membershipStatus == MembershipStatus.SILVER) {
                paid++;
            }
        }

        double conversionRate = (paid  / (double)totalMembers) * 100.0; // quazi_change1_double
        return new MembershipStatistics(totalMembers, paid, conversionRate);
    }
//quazi practice on 20 april
//2-1: Implement addWorkout
//    - Add workout only if member exists
//    - If member does not exist, ignore

//2-2: Implement getAverageWorkoutDurations

//    - Return average workout duration per member
//    - Key: memberId
//    - Value: average duration
//    - duration = endTime - startTime


// quazi practice here on 20 april

    // ❌ IMPLEMENT THIS
    void addWorkout(int memberId, Workout workout){
        for(Member m: members){
            if(m.memberId == memberId){
                workoutsByMember.computeIfAbsent(memberId, k-> new ArrayList<>()).add(workout);
                break;
            }
        }
    }


    // ❌ IMPLEMENT THIS
    public Map<Integer, Double> getAverageWorkoutDurations() {
        // TODO: return avg duration per member
        Map<Integer, Double> result = new HashMap<>();
        for(Integer memberId : workoutsByMember.keySet()){
            int total = 0;
            List<Workout> workoutList = workoutsByMember.get(memberId);

            for(Workout w : workoutList){
                total += w.getDuration();
            }
            result.put(memberId, total/(double)workoutList.size());
        }
        return  result;

    }

    public Map<Integer, Integer> getDuePayments() {
        Map<Integer, Integer> result = new HashMap<>(); //result -> memberId, payment
//        loop through members
        for(Member member : members){
            //        fetch workout
            List<Workout> workouts = workoutsByMember.getOrDefault(member.memberId, new ArrayList<>());
            //        sort workout
            workouts.sort(Comparator.comparing(w->w.workoutId));
            //        ac memberhip status select free workout and hourly charge
            int freeWorkouts = 0;
            int cost = 0;
            switch (member.membershipStatus){
                case GOLD :
                    freeWorkouts = 5;
                    cost = 6;
                    break;
                case SILVER:
                    freeWorkouts = 3;
                    cost = 8;
                    break;
                default:
                    freeWorkouts = 1;
                    cost = 10;
                    break;
            }

//        loop through workout to calculate total price(minutes to roundoff in hours)
            int totalDue = 0;
            for(int i=0; i<workouts.size(); i++){
                if(i >= freeWorkouts){
                    int time = workouts.get(i).getDuration();
                    int timeInHours = (int
                            )Math.ceil(time/60.0);
                    totalDue += timeInHours*cost;
                }
            }
            result.put(member.memberId, totalDue);
//        store in result
        }
//        return result
        return result;
    }


    public void updateMembership(int memberId, MembershipStatus status) {
        for (Member m : members) {
            if (m.memberId == memberId) {
                m.membershipStatus = status;
                return;
            }
        }
    }
}

public class GymMembership {

    public static void main(String[] args) {
        testMember();
        testMembership();
        testGetAverageWorkoutDurations();
        testGetDuePayments();
        System.out.println(" All tests passed successfully!");
    }

    public static void testMember() {
        Member m = new Member(1, "John Doe", MembershipStatus.BRONZE);
        assert m.memberId == 1;
        assert m.name.equals("John Doe");
        assert m.membershipStatus == MembershipStatus.BRONZE;
    }

    public static void testMembership() {
        Membership ms = new Membership();
        ms.addMember(new Member(1, "A", MembershipStatus.BRONZE));
        ms.updateMembership(1, MembershipStatus.SILVER);

        ms.addMember(new Member(2, "B", MembershipStatus.BRONZE));
        ms.addMember(new Member(3, "C", MembershipStatus.GOLD));
        ms.addMember(new Member(4, "D", MembershipStatus.SILVER));
        ms.addMember(new Member(5, "E", MembershipStatus.BRONZE));

        MembershipStatistics s = ms.getMembershipStatistics();
        assert s.totalMembers == 5;
        assert s.totalPaidMembers == 3;
        assert Math.abs(s.conversionRate - 60.0) < 0.1;
    }

    public static void testGetAverageWorkoutDurations() {
        Membership ms = new Membership();

        ms.addMember(new Member(12, "A", MembershipStatus.SILVER));
        ms.addMember(new Member(22, "B", MembershipStatus.BRONZE));
        ms.addMember(new Member(31, "C", MembershipStatus.GOLD));

        ms.addWorkout(12, new Workout(11, 10, 20));
        ms.addWorkout(12, new Workout(47, 100, 155));
        ms.addWorkout(12, new Workout(78, 1000, 1010));

        ms.addWorkout(22, new Workout(24, 15, 35));
        ms.addWorkout(22, new Workout(56, 120, 200));

        ms.addWorkout(31, new Workout(32, 45, 90));
        ms.addWorkout(31, new Workout(62, 300, 400));

        Map<Integer, Double> avg = ms.getAverageWorkoutDurations();
        assert Math.abs(avg.get(12) - 25.0) < 0.1;
        assert Math.abs(avg.get(22) - 50.0) < 0.1;
        assert Math.abs(avg.get(31) - 72.5) < 0.1;
    }

    public static void testGetDuePayments() {
        Membership ms = new Membership();

        ms.addMember(new Member(1, "John", MembershipStatus.BRONZE));
        ms.addMember(new Member(2, "Alex", MembershipStatus.SILVER));
        ms.addMember(new Member(3, "Marie", MembershipStatus.GOLD));

        ms.addWorkout(1, new Workout(1, 500, 700));
        ms.addWorkout(1, new Workout(10, 300, 350));
        ms.addWorkout(1, new Workout(12, 10, 20));
        ms.addWorkout(1, new Workout(3, 50, 90));
        ms.addWorkout(1, new Workout(6, 130, 150));
        ms.addWorkout(1, new Workout(15, 900, 920));

        ms.addWorkout(2, new Workout(13, 510, 540));
        ms.addWorkout(2, new Workout(14, 600, 700));
        ms.addWorkout(2, new Workout(2, 15, 35));
        ms.addWorkout(2, new Workout(4, 100, 155));
        ms.addWorkout(2, new Workout(18, 200, 225));
        ms.addWorkout(2, new Workout(8, 1050, 1155));

        ms.addWorkout(3, new Workout(5, 120, 135));
        ms.addWorkout(3, new Workout(17, 140, 190));
        ms.addWorkout(3, new Workout(9, 210, 255));
        ms.addWorkout(3, new Workout(11, 400, 450));
        ms.addWorkout(3, new Workout(16, 910, 940));
        ms.addWorkout(3, new Workout(7, 1000, 1100));

        Map<Integer, Integer> due = ms.getDuePayments();
        assert Math.abs(due.get(1) - 50) < 0.1;
        assert Math.abs(due.get(2) - 32) < 0.1;
        assert Math.abs(due.get(3) - 6) < 0.1;

        ms.addMember(new Member(4, "Ron", MembershipStatus.SILVER));
        Map<Integer, Integer> due2 = ms.getDuePayments();
        assert due2.get(4) == 0;
    }
}