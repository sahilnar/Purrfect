<template>
    <div class="content"> <!-- DON'T DELETE THIS DIV. ALL THE CONTENT MUST GO INSIDE -->
        <div class="wrapperCreatePost d-flex flex-column justify-content-center align-items-center">
            <form>
                <div class="mb-3">
                    <label for="jobTitle" class="form-label">Gig Title</label>
                    <input type="text" class="form-control" id="jobTitle" v-model="createPostRequest.job.title"
                        style="width: 800px;">
                </div>
                <div class="mb-3">
                    <label for="jobType" class="form-label">Type</label>
                    <select class="form-select" v-model="createPostRequest.job.kindOfJob">
                        <option value="Cat">Cat</option>
                        <option value="Dog">Dog</option>
                        <option value="Hamster">Hamster</option>
                        <option value="Turtle">Turtle</option>
                        <option value="Other">Other</option>
                    </select>
                </div>
                <div class="mb-3">
                    <label for="hourlyRate" class="form-label">Hourly Rate</label>
                    <input type="number" min="1.00" step="0.50" class="form-control" id="hourlyRate"
                        v-model="createPostRequest.job.hourRate">
                </div>
                <div class="mb-3">
                    <label for="startDate" class="form-label">Start Date</label>
                    <input type="date" class="form-control" id="startDate" :min="minStartDate"
                        v-model="createPostRequest.job.jobStartDate" />
                </div>
                <div class="mb-3">
                    <label for="finishDate" class="form-label">Finish Date</label>
                    <input type="date" class="form-control" id="finishDate" :min="minStartDate"
                        v-model="createPostRequest.job.jobFinishDate" />
                </div>
                <div class="mb-3">
                    <label for="startTime" class="form-label">Start Time</label>
                    <input type="time" class="form-control" id="startTime"
                        v-model="createPostRequest.job.startTime" />
                </div>
                <div class="mb-3">
                    <label for="endTime" class="form-label">End Time</label>
                    <input type="time" class="form-control" id="endTime"
                        v-model="createPostRequest.job.endTime" />
                </div>
                <!-- New image upload field -->
                <div class="mb-3">
                    <label for="jobImage" class="form-label">Job Image</label>
                    <input type="file" class="form-control" id="jobImage" @change="handleImageUpload" 
                    />
                </div>
                <div class="mb-3">
                    <label for="description" class="form-label">Description</label>
                    <textarea class="form-control" id="description" rows="5"
                        v-model="createPostRequest.job.description"></textarea>
                </div>
                <div class="mb-3">
                    <label for="totalHours" class="form-label">Total Hours: &nbsp;</label>
                    <span>{{ totalHours }}</span> &nbsp;&nbsp;&nbsp; 
                    <label for="totalCost" class="form-label">Total Pay: &nbsp;</label>
                    <span>{{ totalCost }}</span>
                </div>
                <p id="incorrectInput" style="position:absolute; margin-left: 100px; height: 20px;">{{ message }}</p>
                <button type="submit" class="btn btn-dark btn-purrfect-primary" id="btnSubmit"
                    @click="createPost">Create</button>

            </form>
        </div>

    </div>
</template>

<script>
import JobPostService from "../services/JobPostService";
export default {
    name: "createPost",
    data() {
        return {
            minStartDate: new Date().toISOString().slice(0, 10),
            currentUser: null,
            imageFile: null,
            createPostRequest: {
                //   currentUser: null,
                userId: 0,
                job: {
                    title: "",
                    postingDate: new Date().toISOString().slice(0, 10),
                    hourRate: 5.0,
                    kindOfJob: "",
                    description: "",
                    jobStartDate: new Date().toISOString().slice(0, 10),
                    jobFinishDate: new Date().toISOString().slice(0, 10),
                    startTime: "",
                    endTime: "",
                    image: ""
                }
            },
            message: "",
            totalHours: 0,
            totalCost: 0,
        }
    },
    watch: {
        'createPostRequest.job.startTime': 'updateTotalHoursAndCost',
        'createPostRequest.job.endTime': 'updateTotalHoursAndCost',
        'createPostRequest.job.jobStartDate': 'updateTotalHoursAndCost',
        'createPostRequest.job.jobFinishDate': 'updateTotalHoursAndCost',
    },
    methods: {
        handleImageUpload(event) {
            const file = event.target.files[0];
            if (file) {
                this.imageFile = file;
                const reader = new FileReader();
                reader.onload = (e) => {
                    this.createPostRequest.job.image = e.target.result.split(",")[1]; // extract Base64 data
                // Now, this.imageUrl contains the Base64-encoded image data
                };

                reader.readAsDataURL(file);
            }
        },
        createPost(event) {
            event.preventDefault();
            if (this.createPostRequest.job.title == "" || this.createPostRequest.job.hourRate == "" || this.createPostRequest.job.kindOfJob == "" || this.createPostRequest.job.description == "") {
                this.message = "Please fill out all required fields.";
            } else if (new Date(this.createPostRequest.job.jobFinishDate) < new Date(this.createPostRequest.job.jobStartDate)) {
                this.message = "Finish date cannot be less than start date.";
            } else {
                this.createPostRequest.userId = this.currentUser.userId;
                JobPostService.createPost(this.createPostRequest)
                    .then(response => {
                        let job = response.data;
                        console.log(job);
                        this.message = job;
                        this.$router.push({ name: "listPostedGigs" });

                    })
                    .catch(error => {
                        this.message = error.response.data.message;
                        console.log(error.response.data);
                    })
            }
        },
        getCurrentUser() {
            this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
            if (this.currentUser != null) {
                this.disabled = "";
            }
            console.log(this.currentUser);
        },
        updateTotalHoursAndCost() {
            // Clear the error message when any relevant field changes
            this.message = "";
            if (this.isInvalidTimeRange()) {
                this.message = "Start time cannot be later than end time.";
            } else {
                this.totalHours = this.calculateTotalHours();
                this.totalCost = this.calculateTotalCost();
            }
        },
        isInvalidTimeRange() {
            const startDateTime = new Date(
                `${this.createPostRequest.job.jobStartDate} ${this.createPostRequest.job.startTime}`
            );
            const endDateTime = new Date(
                `${this.createPostRequest.job.jobFinishDate} ${this.createPostRequest.job.endTime}`
            );
            return startDateTime >= endDateTime;
        },
        calculateTotalHours() {
            const startDateTime = new Date(
            `${this.createPostRequest.job.jobStartDate} ${this.createPostRequest.job.startTime}`
            );
            const endDateTime = new Date(
                `${this.createPostRequest.job.jobFinishDate} ${this.createPostRequest.job.endTime}`
            );
            const timeDiff = Math.abs(endDateTime - startDateTime);
            const hours = Math.ceil(timeDiff / (1000 * 60 * 60));
            return hours;
        },
        calculateTotalCost() {
            const totalHours = this.calculateTotalHours();
            const hourlyRate = parseFloat(this.createPostRequest.job.hourRate);
            const totalCost = totalHours * hourlyRate;
            return totalCost.toFixed(2); // Return the total cost rounded to two decimal places
        },
    },
    mounted() {
        this.message = "";
        this.getCurrentUser();

    }
}
</script>

<style>
.wrapperCreatePost {
    background-color: white;
    border-radius: 20px;
    padding: 50px;
}

#incorrectInput {
    color: red;
    margin-top: 10px;
}
</style>