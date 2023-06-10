package ntu.nthuy.recipeapp;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ntu.nthuy.recipeapp.Listeners.InstructionsListener;
import ntu.nthuy.recipeapp.Listeners.RandomRecipeResponseListener;
import ntu.nthuy.recipeapp.Listeners.RecipeDetailsListener;
import ntu.nthuy.recipeapp.Listeners.SimilarRecipesListener;
import ntu.nthuy.recipeapp.Model.InstructionsReponse;
import ntu.nthuy.recipeapp.Model.RandomRecipeApiResponse;
import ntu.nthuy.recipeapp.Model.RecipeDetailsResponse;
import ntu.nthuy.recipeapp.Model.SimilarRecipesResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RequestManager {
    // Một đối tượng Context để lấy giá trị của api_key
    // bằng phương thức `getString() từ tệp strings.xml
    Context context;
    // Một đối tượng Retrofit được sử dụng để tạo ra các yêu cầu API đến Spoonacular API
    Retrofit myRetrofit = new Retrofit.Builder()
            .baseUrl(" https://api.spoonacular.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    // Phương thức khởi tạo: sử dụng trong việc lấy giá trị của api_key
    public RequestManager(Context context) {
        this.context = context;
    }
    // Để lấy danh sách các công thức nấu ăn ngẫu nhiên từ Spoonacular API
    // nhận vào một đối tượng RandomRecipeResponseListener để lắng nghe kết quả trả về
    // và một danh sách các tag để lọc kết quả trả về
    public void getRandomRecipes(RandomRecipeResponseListener listener, List<String> tags){
        // Tạo ra một yêu cầu API đến Spoonacular API
        CallRandoms callRandom = myRetrofit.create(CallRandoms.class);
        Call<RandomRecipeApiResponse> myCall = callRandom.callRandomRecipe(context.getString(R.string.api_key), "20", tags);
        // gọi phương thức enqueue() để thực hiện yêu cầu này bất đồng bộ
        myCall.enqueue(new Callback<RandomRecipeApiResponse>() {
            @Override
            public void onResponse(Call<RandomRecipeApiResponse> call, Response<RandomRecipeApiResponse> response){
                if(!response.isSuccessful()){
                    // yêu cầu API KHÔNG thành công
                    listener.err(response.message());
                    return;
                }
                    listener.fetch(response.body(), response.message());
            }
            @Override
            public void onFailure(Call<RandomRecipeApiResponse> call, Throwable t) {
                listener.err(t.getMessage());
            }
        });
    }
    // Lắng nghe kết quả trả về từ API và một id
    // để xác định công thức nấu ăn cần lấy thông tin chi tiết
    public void getRecipeDetails(RecipeDetailsListener listener, int id){
        // Tạo ra một yêu cầu API đến Spoonacular API
        CallDetails callDetails = myRetrofit.create(CallDetails.class);
        Call<RecipeDetailsResponse> myCall = callDetails.callRecipeDetails(id, context.getString(R.string.api_key));
        // Thực hiện yêu cầu này bất đồng bộ
        myCall.enqueue(new Callback<RecipeDetailsResponse>() {
            @Override
            public void onResponse(Call<RecipeDetailsResponse> call, Response<RecipeDetailsResponse> response) {
                if(response.isSuccessful()){
                    // yêu cầu API thành công
                    listener.fetch(response.body(), response.message());
                    return;
                }
                listener.err(response.message());
            }
            @Override
            public void onFailure(Call<RecipeDetailsResponse> call, Throwable t) {
                listener.err(t.getMessage());
            }
        });
    }
    // Phương thức này nhận vào một đối tượng SimilarRecipesListener
    // để lắng nghe kết quả trả về từ API và một id
    // để xác định các công thức nấu ăn tương tự
    public void getSimilar(SimilarRecipesListener listener, int id){
        // Tạo ra một yêu cầu API đến Spoonacular API
        CallSimilars callSimilar = myRetrofit.create(CallSimilars.class);
        Call<List<SimilarRecipesResponse>> myCall = callSimilar.callSimilarRecipes(id, "10", context.getString(R.string.api_key));
        // Thực hiện yêu cầu này bất đồng bộ
        myCall.enqueue(new Callback<List<SimilarRecipesResponse>>() {
            @Override
            public void onResponse(Call<List<SimilarRecipesResponse>> call, Response<List<SimilarRecipesResponse>> response) {
                if (response.isSuccessful()){
                    // yêu cầu API thành công
                    listener.fetch(response.body(), response.message());
                    return;
                }
                listener.err(response.message());
            }
            @Override
            public void onFailure(Call<List<SimilarRecipesResponse>> call, Throwable t) {
                listener.err(t.getMessage());
            }
        });
    }
    // Lắng nghe kết quả trả về từ API và một id
    // để xác định các hướng dẫn nấu ăn
    public void getInstructions(InstructionsListener listener, int id){
        // Tạo ra một yêu cầu API đến Spoonacular API
        CallInstructions callSteps = myRetrofit.create(CallInstructions.class);
        Call<List<InstructionsReponse>> myCall = callSteps.callInstructions(id, context.getString(R.string.api_key));
        // Thực hiện yêu cầu này bất đồng bộ
        myCall.enqueue(new Callback<List<InstructionsReponse>>() {
            @Override
            public void onResponse(Call<List<InstructionsReponse>> call, Response<List<InstructionsReponse>> response) {
                if(response.isSuccessful()){
                    // yêu cầu API thành công
                    listener.fetch((ArrayList<InstructionsReponse>) response.body(), response.message());
                    return;
                }
                listener.err(response.message());
            }
            @Override
            public void onFailure(Call<List<InstructionsReponse>> call, Throwable t) {
                listener.err(t.getMessage());
            }
        });
    }
    // Định nghĩa các yêu cầu API đến Spoonacular API
    private interface CallRandoms{
        @GET("recipes/random")
        Call<RandomRecipeApiResponse> callRandomRecipe(
                @Query("apiKey") String apiKey,
                @Query("number") String number,
                @Query("tags") List<String> tags
        );
    }
    private interface CallDetails{
        @GET("recipes/{id}/information")
        Call<RecipeDetailsResponse> callRecipeDetails(
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );
    }
    private interface CallSimilars{
        @GET("recipes/{id}/similar")
        Call<List<SimilarRecipesResponse>> callSimilarRecipes(
                @Path("id") int id,
                @Query("number") String number,
                @Query("apiKey") String apiKey
        );
    }
    private interface CallInstructions{
        @GET("recipes/{id}/analyzedInstructions")
        Call<List<InstructionsReponse>> callInstructions(
                @Path("id") int id,
                @Query("apiKey") String apiKey
        );
    }
}
