package com.example.JobFinder.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.JobFinder.domain.Skill;
import com.example.JobFinder.domain.Subscriber;
import com.example.JobFinder.repository.JobRepository;
import com.example.JobFinder.repository.SkillRepository;
import com.example.JobFinder.repository.SubscriberRepository;

@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;
    private final JobRepository jobRepository;

    public SubscriberService(
            SubscriberRepository subscriberRepository,
            SkillRepository skillRepository,
            JobRepository jobRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
        this.jobRepository = jobRepository;
    }

    public boolean isExistsByEmail(String email) {
        return subscriberRepository.existsByEmail(email);
    }

    public Subscriber create(Subscriber subs) {
        // check skills
        if (subs.getSkills() != null) {
            List<Long> reqSkills = subs.getSkills().stream()
                    .map(Skill::getId)
                    .collect(Collectors.toList());

            List<Skill> dbSkills = skillRepository.findByIdIn(reqSkills);
            subs.setSkills(dbSkills);
        }

        return subscriberRepository.save(subs);
    }

    public Subscriber update(Subscriber subsDB, Subscriber subsRequest) {
        // check skills
        if (subsRequest.getSkills() != null) {
            List<Long> reqSkills = subsRequest.getSkills().stream()
                    .map(Skill::getId)
                    .collect(Collectors.toList());

            List<Skill> dbSkills = skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(dbSkills);
        }
        return subscriberRepository.save(subsDB);
    }

    public Subscriber findById(long id) {
        Optional<Subscriber> subsOptional = subscriberRepository.findById(id);
        return subsOptional.orElse(null);
    }

    public Subscriber findByEmail(String email) {
        return subscriberRepository.findByEmail(email);
    }

    // @Scheduled(cron = "*/10 * * * * *")
    // public void testCron() {
    // System.out.println(">>> TEST CRON");
    // }
}
