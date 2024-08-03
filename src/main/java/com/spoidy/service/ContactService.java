package com.spoidy.service;

import com.spoidy.domain.Contact;
import com.spoidy.repos.ContactRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;


import static com.spoidy.constant.Constant.PHOTO_DIRECTORY;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional(rollbackFor = Exception.class)
public class ContactService {

    @Autowired
    private ContactRepo contactRepo;

    public Page<Contact> getAllContacts(int pages,int size) {
        return contactRepo.findAll(PageRequest.of(pages, size, Sort.by("name")));
    }

    public Contact getContactById(String id) throws Exception {
        return contactRepo.findById(id).orElseThrow(
                () -> new Exception("Contact Not Found")
        );
    }

    public Contact addContact(Contact contact) throws Exception {
        return contactRepo.save(contact);
    }
    public void deleteContact(String id) throws Exception {
        contactRepo.deleteById(id);
    }

    public String uploadPhoto(String id,MultipartFile file) throws Exception {
        Contact contact = getContactById(id);
        String url = photoFunction.apply(id,file);
        contact.setPhotoUrl(url);
        contactRepo.save(contact);

        return url;
    }

    private final Function<String,String> fileExtension = fileName -> Optional
                .of(fileName)
                .filter(name -> name.contains("."))
                .map(
                    name -> "." + name
                            .substring(fileName.lastIndexOf(".") + 1))
                .orElse(".png");



    private final BiFunction<String,MultipartFile,String> photoFunction =  (id,image) -> {
        try{
            Path fileStorageLocation = Paths.get(PHOTO_DIRECTORY)
                                        .toAbsolutePath()
                                        .normalize();
            if(!Files.exists(fileStorageLocation)) {
                Files.createDirectories(fileStorageLocation);
            }
            var name = fileExtension
                    .apply(image
                            .getOriginalFilename());
            Files.copy(image.getInputStream(),fileStorageLocation.resolve(id+name),REPLACE_EXISTING);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/contacts/image/" + id + name)
                    .toUriString();
        }catch (Exception e){
            throw new RuntimeException("Unable to Save Image");
        }
    };

    public Map<String, String> deleteUser(String id) throws Exception {
        contactRepo.deleteById(id);
        Map<String,String> map = new HashMap<>();

        map.put("status","Deleted");
        map.put("id",id);
        return map;
    }
}















