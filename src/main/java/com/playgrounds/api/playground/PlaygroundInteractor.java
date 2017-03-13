package com.playgrounds.api.playground;

import com.playgrounds.api.playground.model.Playground;
import com.playgrounds.api.playground.model.Rate;
import com.playgrounds.api.playground.model.Report;
import com.playgrounds.api.playground.web.PlaygroundNotFoundException;
import com.playgrounds.api.user.web.UserRateExistException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * Created by chris on 29/9/2016.
 */
public class PlaygroundInteractor {
    private Playground playground;
    private final static String hostURL = "http://localhost:8080/playgrounds/images/";
    //private PlaygroundService service;
    public PlaygroundInteractor(){
    }


    private boolean testImage(URL image){
        try{
            BufferedImage testingImage = ImageIO.read(image);
            if(testingImage != null) return true;
            else return false;
        } catch (IOException e) {
            return false;
        }
    }

    public void checkIfRateExist(Playground playground, Rate rate) {
        String user_id = rate.getUser();
        if (playground.getRate().size() > 0) {
            ListIterator<Rate> list = playground.getRate().listIterator();
            while (list.hasNext()) {
                Rate rateSearch = list.next();
                if (rateSearch.getUser().equals(rate.getUser())) throw new UserRateExistException(user_id);
            }
        }
        //playground.getRate().add(rate);
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

    public URL addImage(Playground playground, String image_id) throws MalformedURLException {
        String imagePath = hostURL + image_id;
        URL imageURL = new URL(imagePath);
        playground.getImages().add(imageURL);
        if (playground.getImageURL() == null) playground.setImageURL(imageURL);
        return imageURL;
    }

    public String uploadedImageName(Playground playground){
        int number = playground.getImages().size()+1;
        String name = playground.getName()+"_image"+number+".jpg";
        return name;
    }
}
