package vn.binhnc.banhanga.retrofit;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import vn.binhnc.banhanga.model.NotiResponse;
import vn.binhnc.banhanga.model.NotiSendData;

// Gửi thông báo đến app (Từ user <-> admin)
public interface ApiPushNotification {
    @Headers(
            {
                    "Content-Type: application/json",
                    "Authorization: key=AAAA2jcnMG4:APA91bEdPQkiNM3XvzX3IeDjS1mOvvUWhqGM_2u6MK_hjpVGOTkRhyNYeMJLxhN7ZMbx7D46qSWTQ_5NzyGdbArw8fhxuYG95iy-nuVcRd6lGCsuozkFJGtdWNRS3J78DauFaYTHhPLl"
            }
    )
    @POST("fcm/send")
    Observable<NotiResponse> sendNotification(@Body NotiSendData data);
}
