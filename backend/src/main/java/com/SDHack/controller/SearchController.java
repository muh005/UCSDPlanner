package com.SDHack.controller;

import com.SDHack.EventsClass.EventResult;
import com.SDHack.PortalNLP.PortalNLP;
import com.SDHack.SpringDataRepository.FavoritesRepository.Favorite;
import com.SDHack.SpringDataRepository.FavoritesRepository.FavoriteRepository;
import com.SDHack.queryAPI.MacysAPI;
import com.SDHack.queryAPI.YelpCrawler;
import com.SDHack.queryAPI.ticketmaster.TicketMasterAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class SearchController {

    @Autowired
    FavoriteRepository favoriteRepository;

    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/search")
    List<EventResult> getIdByValue(@RequestParam String type
            , @RequestParam String userId ) throws Exception {
        type = type.toLowerCase();
        System.out.println("type is " + type);

        String startTime = "1539485667";

        type = type.replaceAll("%20", " ");
        String typecopy = type;
        type = PortalNLP.classify(type);

        //find the result
        System.out.println("type is " + type);
        List<EventResult> ans;
        if(type.equals("0")) {
            List<String> keyWordList = new ArrayList<>();
            keyWordList.add("concert");
            keyWordList.add("movie");
            keyWordList.add("football");
            keyWordList.add("opera");
            String keyWord = "";
            for(int i = 0 ; i < keyWordList.size() ; i++) {
                if(typecopy.contains(keyWordList.get(i))) {
                    keyWord = keyWordList.get(i);
                    break;
                }
            }
            ans = TicketMasterAPI.search(keyWord);
        } else if(type.equals("1")) {
            ans = YelpCrawler.search(Integer.parseInt(startTime),0);
        } else {
            //Put more types in the list
//            List<String> types = new ArrayList();
//            types.add("eyeliner");
//            types.add("glass");
//            types.add("shoes");
//            types.add("adidas");
//            types.add("nike");
//            types.add("necklace");
//            types.add("perfume");
//            types.add("handbag");
//            types.add("bedding");
//
//            String searchKeyword = "clothes";
//            for(int i = 0; i < types.size(); i ++) {
//                if (type.contains(types.get(i))) {
//                    searchKeyword = types.get(i);
//                    break;
//                }
//            }
            String splitType[] = typecopy.split(" ");
            String searchKeyword = splitType[splitType.length-1];


            ans = MacysAPI.search(searchKeyword);
        }
        for(int i = 0 ; i < ans.size() ; i++) {
            ans.get(i).setId(i);
            ans.get(i).setCategory(Integer.parseInt(type));
        }

        //update the history
        Optional<Favorite> favoriteOptional = favoriteRepository.findById(userId);
        if(favoriteOptional.isPresent()) {
            Favorite favorite = favoriteOptional.get();
            favorite.getIdSet().clear();
            favorite.setHistory(ans);
            favoriteRepository.save(favorite);
        } else {
            Favorite favorite = new Favorite(userId);
            favorite.setHistory(ans);
            favoriteRepository.save(favorite);
        }
        return ans;
    }

}