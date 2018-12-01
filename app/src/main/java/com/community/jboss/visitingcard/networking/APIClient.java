package com.community.jboss.visitingcard.networking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIClient
{
    @POST("/visiting-card")
    Call<VisitingCard> uploadVisitingCard(@Body VisitingCard card);

    @GET("/visiting-card")
    Call<VisitingCard> getVisitingCard();
}
