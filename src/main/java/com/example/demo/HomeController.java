package com.example.demo;

import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.validation.Valid;
import java.io.IOException;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    MessageRepository messageRepository;

    /*This is to upload the image file*/
    @Autowired
    CloudinaryConfig cloudc;

    @RequestMapping("/")
    public String listMessages(Model model){
        model.addAttribute("messages", messageRepository.findAll());

        return "list";
    }
    @GetMapping("/add")
    public String newMessage(Model model){
        model.addAttribute("message", new Message());
        System.out.println("am in get-add");
        return "form";
    }
    @PostMapping("/add")
    public String processMessage(@Valid @ModelAttribute("message") Message message,
                                 @RequestParam("file")MultipartFile file, BindingResult result) {

        if (result.hasErrors()) {
            System.out.println("There is some Error! Ashu");
            return "redirect:/add";
        }
        if(message.getHeadshot() != null && file.isEmpty()){
            System.out.println(message.getHeadshot());
            messageRepository.save(message);
            return "redirect:/";
        }

        Map uploadResult;
            try {
                //upload(object file, map options)
                //file.getBytes() is object and ObjectUtils.asMap(--,--) is the option
                System.out.println("am in post/add - try");
                uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));



            } catch (IOException e) {

                e.printStackTrace();

                return "redirect:/add";
            }
        message.setHeadshot(uploadResult.get("url").toString());
        messageRepository.save(message);
        return "redirect:/";

    }

    @RequestMapping("/detail/{id}")
    public String showMessage(@PathVariable("id") long id, Model model){
        model.addAttribute("message", messageRepository.findById(id).get());
        return "show";
    }

    @RequestMapping("/update/{id}")
    public String updateMessage(@PathVariable("id") long id, Model model) {

        model.addAttribute("message", messageRepository.findById(id).get());

        System.out.println("am in update-id");

        return "form";
    }

    @RequestMapping("/delete/{id}")
    public String delMessage(@PathVariable("id") long id){
        messageRepository.deleteById(id);
        return "redirect:/";
    }


}
