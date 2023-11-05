package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        // finding user
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();

        //find total amount paid for subscription
        int totalAmount=0;
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)){
            totalAmount=500+(subscriptionEntryDto.getNoOfScreensRequired()*250);
        }else if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)){
            totalAmount=800+(subscriptionEntryDto.getNoOfScreensRequired()*250);
        }else{
            totalAmount=1000+(subscriptionEntryDto.getNoOfScreensRequired()*350);
        }


        Subscription subscription =new Subscription();
                subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
                subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
                subscription.setUser(user);
                subscription.setTotalAmountPaid(totalAmount);

        user.setSubscription(subscription);
        return subscriptionRepository.save(subscription).getId();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository


        //find the user;
        User user=userRepository.findById(userId).get();
        //find the Subscription
        Subscription currSubscription=user.getSubscription();
        //current subscription type
        SubscriptionType currSubscriptionType=currSubscription.getSubscriptionType();

        if(currSubscriptionType.equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }

        int changeInPrice=0;
        int totalPrice=0;
        if(currSubscriptionType.equals(SubscriptionType.BASIC)){
            currSubscription.setSubscriptionType(SubscriptionType.PRO);
            int currentPrice=currSubscription.getTotalAmountPaid();
            totalPrice=800+currSubscription.getNoOfScreensSubscribed()*250;
            currSubscription.setTotalAmountPaid(totalPrice);
            changeInPrice=totalPrice-currentPrice;
        }else if(currSubscriptionType.equals(SubscriptionType.PRO)){
            currSubscription.setSubscriptionType(SubscriptionType.ELITE);
            int currentPrice=currSubscription.getTotalAmountPaid();
            totalPrice=1000+currSubscription.getNoOfScreensSubscribed()*350;
            currSubscription.setTotalAmountPaid(totalPrice);
            changeInPrice=totalPrice-currentPrice;
        }

        subscriptionRepository.save(currSubscription);

        return changeInPrice;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> allSubscriptions = subscriptionRepository.findAll();
        int totalRevenue=0;
        for(Subscription subscription : allSubscriptions){
            totalRevenue+=subscription.getTotalAmountPaid();
        }

        return totalRevenue;
    }

}
