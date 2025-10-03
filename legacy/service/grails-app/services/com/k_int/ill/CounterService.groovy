package com.k_int.ill;

import com.k_int.institution.Institution;
import com.k_int.institution.InstitutionService;

/**
 * This service takes responsibility manipulating the counters
 */
public class CounterService {

    InstitutionService institutionService;

    public Counter ensureCounter(Institution institution, String context) {
        Institution determinedInstitution = institution == null ? institutionService.getDefaultInstitution() : institution;
        Counter counter = Counter.findByContextAndInstitution(context, determinedInstitution);
        if (counter == null) {
            String description = context;
            switch (context) {
                case constants.Counter.COUNTER_ACTIVE_BORROWING:
                    description= 'Current (Aggregate) Borrowing Level';
                    break;

                case constants.Counter.COUNTER_ACTIVE_LOANS:
                    description = 'Current (Aggregate) Lending Level';
                    break;

                default:
                    break;
            }

            counter = new Counter(
                institution: determinedInstitution,
                context: context,
                value: 0,
                description: description
            );
            counter.save(flush:true, failOnError:true);
        }
        return(counter);
    }

    public incrementCounter(Institution institution, String context) {
        Counter counter = ensureCounter(institution, context);
        counter.lock();
        counter.value++;
        counter.save(flush:true, failOnError:true);
    }

    public decrementCounter(Institution institution, String context) {
        Counter counter = ensureCounter(institution, context);
        counter.lock();
        counter.value--;
        counter.save(flush: true, failOnError: true);
    }
}
