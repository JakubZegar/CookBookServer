package pl.edu.pwsztar.service.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.edu.pwsztar.domain.dto.*;
import pl.edu.pwsztar.domain.entity.Product;
import pl.edu.pwsztar.domain.entity.Recepie;
import pl.edu.pwsztar.domain.entity.RecepieProducts;
import pl.edu.pwsztar.domain.mapper.CreateRecepieMapper;
import pl.edu.pwsztar.domain.mapper.RecepieListMapper;
import pl.edu.pwsztar.domain.mapper.RecepieMapper;
import pl.edu.pwsztar.domain.mapper.SimpleRecepieListMapper;
import pl.edu.pwsztar.domain.repository.RecepieRepository;
import pl.edu.pwsztar.service.ProductService;
import pl.edu.pwsztar.service.RecepieProductsService;
import pl.edu.pwsztar.service.RecepieService;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class RecepieServiceImpl implements RecepieService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RecepieServiceImpl.class);

    private final RecepieRepository recepieRepository;
    private final CreateRecepieMapper createRecepieMapper;
    private final SimpleRecepieListMapper simpleRecepieListMapper;
    private final RecepieMapper recepieMapper;

    private final RecepieProductsService recepieProductsService;
    private final ProductService productService;

    @Autowired
    public RecepieServiceImpl(RecepieRepository recepieRepository,
                              CreateRecepieMapper createRecepieMapper,
                              SimpleRecepieListMapper simpleRecepieListMapper,
                              RecepieMapper recepieMapper,
                              RecepieProductsService recepieProductsService,
                              ProductService productService) {
        this.recepieRepository = recepieRepository;
        this.createRecepieMapper = createRecepieMapper;
        this.simpleRecepieListMapper = simpleRecepieListMapper;
        this.recepieMapper = recepieMapper;
        this.recepieProductsService = recepieProductsService;
        this.productService = productService;
    }


    @Override
    public RecepieDto getRecepieDetails(Long recepieId) {
        return recepieMapper.convert(recepieRepository.getRecepieDetails(recepieId));
    }

    @Override
    public void save(CreateRecepieDto createRecepieDto){
        this.recepieRepository.save(createRecepieMapper.mapRecepieDtoToRecepie(createRecepieDto));
    }

    @Override
    public void deleteById(Long id) {
        recepieRepository.deleteById(id);
    }

    @Override
    public List<SimpleRecepieDto> getSimpleRecepies() {
        List<Recepie> recepies = recepieRepository.findAll();
        return simpleRecepieListMapper.convert(recepies);
    }

    @Override
    public List<SimpleRecepieDto> filterRecepies(List<ProductDto> productDtoList) {

        List<SimpleRecepieDto> allRecepies = simpleRecepieListMapper.convert(recepieRepository.findAll());
        List<SimpleRecepieDto> filteredRecepies = new ArrayList<>();


        List<ProductDto> requiredProductsForRecepie = new ArrayList<>();

        allRecepies.forEach(recepie ->{
            List<RecepieProductsDto> allProductsRequiredForRecepie = recepieProductsService.getProductsRequiredForRecepie(recepie.getRecepieId());
                allProductsRequiredForRecepie.forEach(productRequired -> requiredProductsForRecepie.add(productService.getRequiredProductDetails(productRequired.getProductId()))
                    );

                if(requiredProductsForRecepie.stream()
                        .allMatch( requiredProductForRecepie ->productDtoList.stream()
                                .anyMatch(userProduct -> {
                                    Optional<RecepieProductsDto> possibleAmount = allProductsRequiredForRecepie.stream()
                                        .filter(a-> a.getProductId().equals(requiredProductForRecepie.getProductId()))
                                        .findAny();

                                    float amount = 1;
                                    if(possibleAmount.isPresent()){
                                        amount = possibleAmount.get().getAmount();
                                    }

                                    return userProduct.getName().equals(requiredProductForRecepie.getName())
                                            && userProduct.getAmount() >= requiredProductForRecepie.getAmount() * amount;
                                })
                        )
                ){
                    filteredRecepies.add(recepie);
                }
            });

        return filteredRecepies;
    }

}
