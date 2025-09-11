package com.pleiades.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Resale Store", description = "중고몰")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/store/resale")
public class ResaleStoreController {

//    @GetMapping("/face")
//    public String getFaceList() {
//
//    }
//
//    @GetMapping("/fashion")
//    public String getFashionList() {
//
//    }
//
//    @GetMapping("/bg")
//    public String getBgList() {
//
//    }
}
