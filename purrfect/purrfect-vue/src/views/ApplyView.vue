<template>
    <div class="content apply-view">
        <div v-if="jobs.length != filterJobs.length" class="wrapperApply">
            <table class="table table-borderless mb-3"
                v-for="(job, index) in jobs.filter(job => !filterJobs.includes(job.jobId))" :key="index">
                <tbody>
                    <tr>
                        <td class="first-row">
                            <h3>{{ job.title }}</h3>
                        </td>
                        <td class="first-row text-end">
                            <span class="badge rounded-pill ">{{ job.kindOfJob}}</span>&nbsp;&nbsp;&nbsp;
                            <span class="badge rounded-pill ">Hourly Rate: ${{ job.hourRate}}</span>&nbsp;&nbsp;&nbsp;
                            <span class="badge rounded-pill ">Total Hours: {{ calculateTotalHours(job) }}</span>&nbsp;&nbsp;&nbsp; 
                            <span class="badge rounded-pill ">Total Pay: ${{ calculateTotalCost(job) }}</span>&nbsp;&nbsp;&nbsp; 
                            <span class="badge rounded-pill ">Posted {{job.postingDate}}</span></td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <img :src="'data:image/jpg;base64,' + job.image" alt="Job Image" class="job-image"/>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <span class="badge rounded-pill ">Start Date: {{job.jobStartDate}}</span> &nbsp;&nbsp;&nbsp; 
                            <span class="badge rounded-pill ">Finish Date: {{job.jobFinishDate}}</span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <span class="badge rounded-pill ">Start Time: {{job.startTime}}</span> &nbsp;&nbsp;&nbsp; 
                            <span class="badge rounded-pill ">Finish time: {{job.endTime}}</span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">{{ job.description }}</td>
                    </tr>
                    <tr>
                        <td class="last-row"><button class="btn btn-dark me-2 btn-purrfect-primary" :id="index"
                                :disabled="job.applied" @click="applyNow(job.jobId, job)">{{ job.applied ? 'Applied' :
                                    'Apply' }}</button></td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="wrapperApply d-flex flex-column align-items-center" v-else>
            <h5>There are no gigs available at this moment.</h5>
        </div>
    </div>
</template>

<script>
import ApplicationService from '../services/ApplicationService';
// import UserApplications from '../UserApplications';
export default {
    name: "applyView",
    data() {
        return {
            jobs: [], //stage variable
            filterJobs: [], //this includes Applications of the currentUser
            currentUser: JSON.parse(localStorage.getItem('currentUser'))
        }
    },
    methods: {
        applyNow(jobId, job) {
            console.log("This is the job id " + jobId);
            // let currentUser = JSON.parse(localStorage.getItem('currentUser'));
            ApplicationService.apply(this.currentUser.userId, jobId)
                .then(response => {
                    const newApplication = response.data;
                    console.log(newApplication);
                    job.applied = true;
                })
                .catch(error => {
                    console.log(error);
                })
        },
        getUserApplications() {
            ApplicationService.getUserApplications(this.currentUser.userId)
                .then(response => {
                    let applications = response.data;
                    for (let i = 0; i < applications.length; i++) {
                        let jobBuster = applications[i].userBuster.userId;
                        console.log("job buster:" + i + " : " + jobBuster);
                        if (jobBuster == this.currentUser.userId) {
                            this.filterJobs.push(applications[i].jobListing.jobId);
                        }
                    }
                })
                .catch(error => {
                    console.log(error);
                })
        },
        showAllJobs() {
            ApplicationService.showJobs()
                .then(response => {
                    this.jobs = response.data;

                    for (let i = 0; i < this.jobs.length; i++) {
                        let jobPoster = this.jobs[i].userPoster.userId;
                        console.log("job poster:" + i + " : " + jobPoster);
                        if (jobPoster == this.currentUser.userId) {
                            this.filterJobs.push(this.jobs[i].jobId);
                        }
                    }
                })
                .catch(error => {
                    console.log(error);
                })
        },
        calculateTotalHours(job) {
            const startDateTime = new Date(`${job.jobStartDate} ${job.startTime}`);
            const endDateTime = new Date(`${job.jobFinishDate} ${job.endTime}`);
            const timeDiff = Math.abs(endDateTime - startDateTime);
            const hours = Math.ceil(timeDiff / (1000 * 60 * 60));
            return hours;
        },
        calculateTotalCost(job) {
            const totalHours = this.calculateTotalHours(job);
            const hourlyRate = parseFloat(job.hourRate);
            const totalCost = totalHours * hourlyRate;
            return totalCost.toFixed(2);
        },
    },
    mounted() {
        this.getUserApplications();
        this.showAllJobs();
    }
}
</script>

<style>
.wrapperApply h5 {
    color: white;
}

table {
    background-color: white;
    border-radius: 20px;
    box-shadow: rgba(0, 0, 0, 0.3) 0px 19px 38px, rgba(0, 0, 0, 0.22) 0px 15px 12px;
}

td {
    padding-left: 50px !important;
    padding-right: 50px !important;
}

.first-row {
    padding-top: 50px !important;
}

.first-row h3 {
    color: rgba(153, 45, 176, 1) !important;
}

.last-row {
    padding-bottom: 50px !important;
}

.wrapperApply .badge {
    background-color: #7c24c4;
}
.job-image {
    width: 80px; /* Set your preferred width */
  height: auto; /* Maintain aspect ratio */
}
</style>

