package com.forgeflow.backend.config;

import com.forgeflow.backend.model.*;
import com.forgeflow.backend.repository.*;
import com.forgeflow.shared.enums.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final WorkflowRepository workflowRepository;
    private final JobRepository jobRepository;
    private final WorkerNodeRepository workerNodeRepository;
    private final AuditLogRepository auditLogRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           WorkflowRepository workflowRepository,
                           JobRepository jobRepository,
                           WorkerNodeRepository workerNodeRepository,
                           AuditLogRepository auditLogRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.workflowRepository = workflowRepository;
        this.jobRepository = jobRepository;
        this.workerNodeRepository = workerNodeRepository;
        this.auditLogRepository = auditLogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            userRepository.save(new User("admin", passwordEncoder.encode("admin123"), "admin@forgeflow.internal", UserRole.ADMINISTRATOR));
            userRepository.save(new User("developer", passwordEncoder.encode("dev123"), "dev@forgeflow.internal", UserRole.DEVELOPER));
            userRepository.save(new User("viewer", passwordEncoder.encode("view123"), "viewer@forgeflow.internal", UserRole.VIEWER));
        }

        if (workflowRepository.count() == 0) {
            Workflow sampleWorkflow = new Workflow("File Processing & Email Pipeline", "Automated file ingest, validation, compilation, deployment and alert notification pipeline", "admin");
            sampleWorkflow.setStatus(WorkflowStatus.ACTIVE);
            sampleWorkflow.setJsonDefinition("{\"nodes\":[{\"id\":\"1\",\"type\":\"RECEIVE_FILE\",\"label\":\"Receive File\"},{\"id\":\"2\",\"type\":\"VALIDATE\",\"label\":\"Validate Format\"},{\"id\":\"3\",\"type\":\"COMPILE\",\"label\":\"Compile Artifact\"},{\"id\":\"4\",\"type\":\"DEPLOY\",\"label\":\"Deploy Target\"},{\"id\":\"5\",\"type\":\"SEND_EMAIL\",\"label\":\"Send Notification\"}],\"edges\":[{\"from\":\"1\",\"to\":\"2\"},{\"from\":\"2\",\"to\":\"3\"},{\"from\":\"3\",\"to\":\"4\"},{\"from\":\"4\",\"to\":\"5\"}]}");
            workflowRepository.save(sampleWorkflow);
        }

        if (jobRepository.count() == 0) {
            Job syncJob = new Job("Nightly LAN File Sync", "0 0 2 * * ?", JobType.FILE_SYNC, "C:/Data/Sync", "{\"folder\":\"shared_docs\"}");
            syncJob.setStatus(JobStatus.SCHEDULED);
            jobRepository.save(syncJob);

            Job metricJob = new Job("Cluster Health Audit", "0 */5 * * * ?", JobType.HTTP_REQUEST, "http://localhost:8080/actuator/health", "{\"method\":\"GET\"}");
            metricJob.setStatus(JobStatus.SCHEDULED);
            jobRepository.save(metricJob);
        }

        if (workerNodeRepository.count() == 0) {
            WorkerNode leader = new WorkerNode("worker-node-1", "127.0.0.1", 8081);
            leader.setStatus(WorkerStatus.LEADER);
            leader.setCpuLoad(12.5);
            leader.setMemoryUsage(42.8);
            workerNodeRepository.save(leader);

            WorkerNode worker2 = new WorkerNode("worker-node-2", "192.168.1.102", 8082);
            worker2.setStatus(WorkerStatus.IDLE);
            worker2.setCpuLoad(5.1);
            worker2.setMemoryUsage(28.4);
            workerNodeRepository.save(worker2);
        }

        if (auditLogRepository.count() == 0) {
            auditLogRepository.save(new AuditLog("admin", "SYSTEM_INIT", "PLATFORM", "ForgeFlow platform initialized successfully with default roles and worker nodes."));
        }
    }
}
