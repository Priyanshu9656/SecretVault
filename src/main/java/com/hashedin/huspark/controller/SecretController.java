package main.java.com.hashedin.huspark.controller;

import com.hashedin.huspark.constants.ApplicationConstants;
import com.hashedin.huspark.dto.PageResponse;
import com.hashedin.huspark.dto.SecretRequest;
import com.hashedin.huspark.dto.SecretResponse;
import com.hashedin.huspark.service.SecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/secret")
public class SecretController {

    @Autowired
    private SecretService secretService;

    // Create a new secret
    @PostMapping("/add")
    public ResponseEntity<SecretResponse> createSecret(@RequestBody SecretRequest secretRequest) {
        SecretResponse createdSecret = secretService.createSecret(secretRequest);
        return new ResponseEntity<>(createdSecret, HttpStatus.CREATED);
    }

    // Get a secret by ID
    @GetMapping("/{id}")
    public ResponseEntity<SecretResponse> getSecretById(@PathVariable Long id) {
        SecretResponse secret = secretService.getSecretById(id);
        return new ResponseEntity<>(secret, HttpStatus.OK);
    }

    // Get all secrets
    @GetMapping("/all")
    public ResponseEntity<List<SecretResponse>> getAllSecrets() {
        List<SecretResponse> secrets = secretService.getAllSecret();
        return new ResponseEntity<>(secrets, HttpStatus.OK);
    }

    // Update a secret
    @PutMapping("/update/{id}")
    public ResponseEntity<SecretResponse> updateSecret(@PathVariable Long id, @RequestBody SecretRequest secretDetails) {
        SecretResponse updatedSecret = secretService.updateSecret(id, secretDetails);
        return new ResponseEntity<>(updatedSecret, HttpStatus.OK);
    }

    // Delete a secret
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteSecretById(@PathVariable Long id){
        boolean response=secretService.deleteSecret(id);
        if(response)
            return new ResponseEntity<>("Secret Deleted Successfully",HttpStatus.OK);
        else
            return new ResponseEntity<>("Secret Deletion Unsuccessful", HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/allSecretPage")
    public ResponseEntity<PageResponse> getAllSecretWithPagination(
            @RequestParam(defaultValue = ApplicationConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = ApplicationConstants.PAGE_SIZE, required = false) Integer pageSize)
    {
        return ResponseEntity.ok(secretService.getAllSecretWithPagination(pageNumber, pageSize));
    }


    @GetMapping("/allSecretPagesBySorting")
    public ResponseEntity<PageResponse> getAllSecretWithPaginationAndSorting(
            @RequestParam(defaultValue = ApplicationConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(defaultValue = ApplicationConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(defaultValue = ApplicationConstants.SORT_BY, required = false) String sortBy,
            @RequestParam(defaultValue = ApplicationConstants.SORT_DIR, required = false) String dir
    ){
        return ResponseEntity.ok(secretService.getAllSecretWithPaginationAndSorting(pageNumber,pageSize,sortBy,dir));
    }

}