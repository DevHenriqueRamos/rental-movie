package com.rentalmovie.rental.configs;

import com.rentalmovie.rental.jobs.RentalExpirationJob;
import org.quartz.SimpleTrigger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetailFactoryBean rentalExpirationJobDetail() {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(RentalExpirationJob.class);
        factoryBean.setDescription("Job que verifica e expira aluguéis");
        factoryBean.setDurability(true);
        return factoryBean;
    }

    @Bean
    public SimpleTriggerFactoryBean rentalExpirationTrigger(JobDetailFactoryBean jobDetailFactoryBean) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(jobDetailFactoryBean.getObject());
        trigger.setRepeatInterval(24 * 60 * 60 * 1000);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
        trigger.setDescription("Trigger diária para expirar aluguéis");
        return trigger;
    }
}
