package com.spoidy.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spoidy.constant.Constant;
import com.spoidy.domain.Contact;
import com.spoidy.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@RestController
@JsonInclude(JsonInclude.Include.NON_NULL)
@RequestMapping("/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<Contact> addContact(@RequestBody Contact contact) throws Exception {
        return ResponseEntity.created(
                URI.create("/contacts/userID")
        ).body(contactService.addContact(contact));
    }

    @GetMapping
    public ResponseEntity<Page<Contact>> getAllContacts(
            @RequestParam(value = "page",defaultValue = "0")
            int page,
            @RequestParam(value = "size", defaultValue = "10")
            int size) throws Exception {
        return ResponseEntity.ok(contactService.getAllContacts(page, size));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable("id") String id) throws Exception {
        return ResponseEntity.ok(contactService.getContactById(id));
    }

    @PutMapping("/photo")
    public ResponseEntity<String> updatePhoto(
            @RequestParam("id")
            String id,
            @RequestParam("file")
            MultipartFile file
    ) throws Exception {
        return ResponseEntity.ok().body(contactService.uploadPhoto(id,file));
    }

    @GetMapping(path = "/image/{filename}",produces = {IMAGE_PNG_VALUE,IMAGE_JPEG_VALUE})
    public byte[] getImage(@PathVariable("filename") String filename) throws Exception {
        return Files.readAllBytes(Paths.get(Constant.PHOTO_DIRECTORY + filename));
    }

    @DeleteMapping
    public ResponseEntity<Map<String,String>> deleteContact(@RequestBody Contact contact) throws Exception {
        return new ResponseEntity<>(
                contactService.deleteUser(contact.getId()),
                HttpStatusCode.valueOf(200)
        );
    }
}
