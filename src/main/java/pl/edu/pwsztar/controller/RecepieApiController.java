package pl.edu.pwsztar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.edu.pwsztar.domain.dto.CreateRecepieDto;
import pl.edu.pwsztar.domain.dto.ProductDto;
import pl.edu.pwsztar.domain.dto.RecepieDto;
import pl.edu.pwsztar.domain.dto.SimpleRecepieDto;
import pl.edu.pwsztar.service.RecepieService;

import java.util.List;

@Controller
@RequestMapping(value="/api")
public class RecepieApiController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RecepieApiController.class);
    private final RecepieService recepieService;
    private List<SimpleRecepieDto> simpleRecepieDtoList;


    @Autowired
    public RecepieApiController(RecepieService recepieService) {
        this.recepieService = recepieService;
    }

    @CrossOrigin
    @GetMapping(value="/recepies",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<SimpleRecepieDto>> getRecepies(){
        LOGGER.info("find all recepies");
        List<SimpleRecepieDto> simpleRecepieDto = recepieService.getSimpleRecepies();
        return new ResponseEntity<>(simpleRecepieDto, HttpStatus.OK);
    }

    @CrossOrigin
    @GetMapping(value="/recepies/{recepieId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RecepieDto> getRecepieDetails(@PathVariable Long recepieId){
        LOGGER.info("Fing recepie details:{} ", recepieId);
        RecepieDto recepieDto = recepieService.getRecepieDetails(recepieId);
        return new ResponseEntity<>(recepieDto, HttpStatus.OK);
    }

    @CrossOrigin
    @DeleteMapping(value="/recepies/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> deleteRecepie(@PathVariable Long id){
        LOGGER.info("Delete recepie");
        recepieService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @CrossOrigin
    @PostMapping(value="/recepies/create",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<?> createRecepie(@RequestBody CreateRecepieDto createRecepieDto){
        LOGGER.info("Create recepie:{}", createRecepieDto);
        recepieService.save(createRecepieDto);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @CrossOrigin
    @RequestMapping(value="/recepies/filter", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST)
    public ResponseEntity<?> filterRecepie(@RequestBody List<ProductDto> productDtoList){
        LOGGER.info("Filtering recepies{}", productDtoList.toString());
        simpleRecepieDtoList = recepieService.filterRecepies(productDtoList);

        return new ResponseEntity<>( HttpStatus.CREATED);
    }

    @CrossOrigin
    @GetMapping(value="/recepies/filtered", produces =  MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<SimpleRecepieDto>> getFiltered(){
        return new ResponseEntity<>(simpleRecepieDtoList, HttpStatus.OK);
    }
}
