package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        //checking that seriesName is present or not
       WebSeries webSeries1=webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
       if(webSeries1!=null){
           throw new Exception("Series is already present");
       }

        //updating the rating in production house;
        ProductionHouse productionHouse=updateProductionHouse(webSeriesEntryDto.getProductionHouseId(),webSeriesEntryDto.getRating());

        //creating webSeries
        WebSeries webSeries=new WebSeries();
              webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
              webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
              webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
              webSeries.setRating(webSeriesEntryDto.getRating());
              webSeries.setProductionHouse(productionHouse);

        //setting FR's
        productionHouse.getWebSeriesList().add(webSeries);
        WebSeries currWeb=webSeriesRepository.save(webSeries);
        return currWeb.getId();

    }
    public ProductionHouse updateProductionHouse(Integer productionHouseId,double rating) throws Exception{
        Optional<ProductionHouse>  optionalProductionHouse=productionHouseRepository.findById(productionHouseId);
        if(!optionalProductionHouse.isPresent()){
            throw new Exception();
        }
        ProductionHouse currProductionHouse=optionalProductionHouse.get();
        List<WebSeries> listOfWebSeries=currProductionHouse.getWebSeriesList();
        double sum=rating;
        for(WebSeries web: listOfWebSeries){
            sum+=web.getRating();
        }
        int totalWeb=listOfWebSeries.size()+1;
        double avgRating=sum/totalWeb;
        currProductionHouse.setRatings(avgRating);
        return productionHouseRepository.save(currProductionHouse);
    }
}
