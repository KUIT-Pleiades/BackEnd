package com.pleiades.controller;

import com.pleiades.dto.store.ListingPriceDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Store", description = "상점 공통 ")
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/store")
public class StoreController {

}
