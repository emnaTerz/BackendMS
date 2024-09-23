package com.emna.micro_service4.client;

import com.emna.micro_service3.dto.UpdateStatusRequest;
import com.emna.micro_service4.model.ValueOfAttribute;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "micro-service2", url = "http://localhost:8082")
public interface IndexConfigurationInterface {


    @GetMapping("/api/config/attributes/{pendingMessageId}")
    List<ValueOfAttribute> findByPendingMessageId(@PathVariable("pendingMessageId") String pendingMessageId);


    @PutMapping("/api/config/update-status/{id}")
    ResponseEntity<String> updateStatusById(@PathVariable("id") String id, @RequestBody UpdateStatusRequest request);
}
