package com.example.projektkalkulationeksamen.comparators;

import com.example.projektkalkulationeksamen.model.Milestone;

import java.util.Comparator;

public class DeadlineComparator implements Comparator<Milestone> {

    @Override
    public int compare(Milestone o1, Milestone o2) {
        // if both don't have a value
        if (o1.getDeadline() == null && o2.getDeadline() == null) {
            return 0;
            // if o1 doesn't have a value
        } else if(o1.getDeadline() == null){
            return 1;
            // if o2 doesn't have a value
        } else if (o2.getDeadline() == null){
            return -1;
        } else {
            // Sorts dates from high -> low
            return o1.getDeadline().compareTo(o2.getDeadline());
        }
    }
}
