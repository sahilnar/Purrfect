import http from "../http-common.js";
class JobPostService{
    createPost(data){
        return http.post("/jobs", data);
    }
}

export default new JobPostService();