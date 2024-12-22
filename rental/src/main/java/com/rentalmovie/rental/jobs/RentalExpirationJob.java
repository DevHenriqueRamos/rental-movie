package com.rentalmovie.rental.jobs;

import com.rentalmovie.rental.services.RentalService;
import lombok.extern.log4j.Log4j2;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RentalExpirationJob implements Job {

    private final RentalService rentalService;

    public RentalExpirationJob(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        rentalService.expireRentals();
        rentalService.nearExpireRentals();
    }
}
