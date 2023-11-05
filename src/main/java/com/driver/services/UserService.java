package com.driver.services;


import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.model.WebSeries;
import com.driver.repository.UserRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebSeriesRepository webSeriesRepository;


    public Integer addUser(User user){

        //Jut simply add the user to the Db and return the userId returned by the repository
        User users=userRepository.save(user);
        return users.getId();
    }

    public Integer getAvailableCountOfWebSeriesViewable(Integer userId){

        //Return the count of all webSeries that a user can watch based on his ageLimit and subscriptionType
        //Hint: Take out all the Webseries from the WebRepository

        User user =userRepository.findById(userId).get();
        Subscription subscription=user.getSubscription();
        SubscriptionType subscriptionType=subscription.getSubscriptionType();


        List<WebSeries> webSeriesList=webSeriesRepository.findAll();

        int countOfBasicWeb=0;
        int countOfProWeb=0;
        int countOfEliteWeb=0;

        for(WebSeries web : webSeriesList){
            if(web.getSubscriptionType().equals(SubscriptionType.BASIC) && web.getAgeLimit()<=user.getAge()){
                countOfBasicWeb++;
            }
            if(web.getSubscriptionType().equals(SubscriptionType.PRO ) && web.getAgeLimit()<=user.getAge()){
                countOfProWeb++;
            }
            if(web.getSubscriptionType().equals(SubscriptionType.ELITE) && web.getAgeLimit()<=user.getAge()){
                countOfEliteWeb++;
            }
        }

        if(subscriptionType.equals(SubscriptionType.BASIC)){
            return countOfBasicWeb;
        }else if(subscriptionType.equals(SubscriptionType.PRO)){
            return countOfBasicWeb+countOfProWeb;
        }else{
            return countOfBasicWeb+countOfProWeb+countOfEliteWeb;
        }

    }


}
