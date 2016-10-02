package com.playgrounds.api.playground;

import com.playgrounds.api.playground.model.Playground;
import com.playgrounds.api.playground.model.Rate;
import com.playgrounds.api.playground.model.Report;
import com.playgrounds.api.playground.web.PlaygroundNotFoundException;
import com.playgrounds.api.user.web.UserRateExistException;

import java.util.Iterator;
import java.util.ListIterator;

/**
 * Created by chris on 29/9/2016.
 */
public class PlaygroundInteractor {
    private Playground playground;
    //private PlaygroundService service;

    public PlaygroundInteractor(){
    }

    public void createPlayground(Playground pl){
        if(pl.getName().isEmpty()) throw new RuntimeException("Name can't be empty");
        if(pl.getCity().isEmpty()) throw new RuntimeException("City name can't be empty");
        if(pl.getAdded_by().isEmpty()) throw new RuntimeException("Added_by user can't be empty");
        //service.create(pl);
    }

    public void addRate(Playground playground, Rate rate){
        String user_id = rate.getUser();
        if(playground.getRate().size() > 0) {
              ListIterator<Rate> list = playground.getRate().listIterator();
            while (list.hasNext()) {
                   Rate rateSearch = list.next();
                if (rateSearch.getUser().equals(rate.getUser())) throw new UserRateExistException(user_id);
                }
        }
        playground.getRate().add(rate);
    }

    public void moveUserRateOnTop(Playground playground, String user_id){
        Iterator it = playground.getRate().iterator();
        while (it.hasNext()) {
            Rate rate1 = (Rate) it.next();
            if (rate1.getUser().equals(user_id)) {
                it.remove();
                playground.getRate().addFirst(rate1);
                break;
            }
        }
    }

    public void addReport(Playground playground, Report report){
        playground.getReports().add(report);
    }

    public void addImage(Playground playground, String image_id){
        playground.getImages().add(image_id);
    }

    public String uploadedImageName(Playground playground){
        int number = playground.getImages().size()+1;
        String name = playground.getName()+"_image"+number+".jpg";
        return name;
    }
}