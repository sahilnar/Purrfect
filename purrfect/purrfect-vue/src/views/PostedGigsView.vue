<template>
    <div class="content posted-gigs-view">
        <div v-if="postedGigsResponse.length > 0" class="wrapperPostedGigs d-flex flex-column align-items-center">
            <table class="table table-borderless mb-3" v-for="job in postedGigsResponse" :key="job.jobId">
                <tbody>
                    <tr>
                        <td class="first-row">
                            <h3 :contenteditable="editMode[job.jobId]" @input="updateJobField(job, 'title', $event)">{{ job.title }}</h3>
                        </td>
                        <td class="first-row text-end">
                            <span class="badge rounded-pill " :contenteditable="editMode[job.jobId]" @input="updateJobField(job, 'kindOfJob', $event)">{{ job.kindOfJob }}</span> &nbsp;&nbsp;&nbsp; 
                            <span class="badge rounded-pill ">
                                <span>Hourly Rate: $</span>
                                <span :contenteditable="editMode[job.jobId]" @input="updateJobField(job, 'hourRate', $event)">{{ job.hourRate }}</span>

                            </span>&nbsp;&nbsp;&nbsp; 
                            <span class="badge rounded-pill ">Total Hours: {{ calculateTotalHours(job) }}</span>&nbsp;&nbsp;&nbsp; 
                            <span class="badge rounded-pill ">Total Pay: ${{ calculateTotalCost(job) }}</span>&nbsp;&nbsp;&nbsp; 
                            <span class="badge rounded-pill ">Posted {{getDaysAgo(job.postingDate) }}</span>
                        </td>
                    </tr>

                    
                    <tr>
                        <td colspan="4">
                            <div v-if="editMode[job.jobId]" class="mb-3">
                                <label for="editJobImage" class="form-label">Edit Job Image</label>
                                <input type="file" class="form-control" id="editJobImage"  @change="updateJobField(job, 'image', $event)" />
                            </div>
                            <img v-if="!editMode[job.jobId]" :src="'data:image/jpg;base64,' + job.image" alt="Job Image" class="job-image"/>
                        </td>
                    </tr>

                    <tr>
                        <td colspan="4">
                            <div v-if="editMode[job.jobId]" class="mb-3">
                                <label for="editStartDate" class="form-label">Edit Start Date</label>
                                <input type="date" class="form-control" id="editStartDate" :value="editMode[job.jobId] ? job.editStartDate : ''" @change="updateJobField(job, 'jobStartDate', $event)" />
                            </div>
                            <span v-else class="badge rounded-pill ">Start Date: {{ job.jobStartDate }}</span> &nbsp;&nbsp;&nbsp; 
                            <div v-if="editMode[job.jobId]" class="mb-3">
                                <label for="editFinishDate" class="form-label">Edit Finish Date</label>
                                <input type="date" class="form-control" id="editFinishDate" :value="editMode[job.jobId] ? job.editFinishDate : ''" @change="updateJobField(job, 'jobFinishDate', $event)" />
                            </div>
                            <span v-else class="badge rounded-pill ">Finish Date: {{ job.jobFinishDate }}</span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <div v-if="editMode[job.jobId]" class="mb-3">
                                <label for="editStartTime" class="form-label">Edit Start Time</label>
                                <input type="time" class="form-control" id="editStartTime" :value="editMode[job.jobId] ? job.editStartTime : ''" @change="updateJobField(job, 'startTime', $event)" />
                            </div>
                            <span v-else class="badge rounded-pill ">Start Time: {{ job.startTime }}</span> &nbsp;&nbsp;&nbsp; 
                            <div v-if="editMode[job.jobId]" class="mb-3">
                                <label for="editEndTime" class="form-label">Edit End Time</label>
                                <input type="time" class="form-control" id="editEndTime" :value="editMode[job.jobId] ? job.editEndTime : ''" @change="updateJobField(job, 'endTime', $event)" />
                            </div>
                            <span v-else class="badge rounded-pill ">End Time: {{ job.endTime }}</span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">
                            <p :contenteditable="editMode[job.jobId]" @input="updateJobField(job, 'description', $event)">{{ job.description }}</p>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="4">
                        <p id="incorrectInput" style="position:absolute; margin-left: 100px; height: 20px;">{{ message }}</p>
                        </td> 
                    </tr>
                    <tr>
                        <td colspan="2">
                            You have <b>{{ appsize[job.jobId] }}</b> applicants.
                        </td>
                        <td class="text-end">
                            <button v-if="!editMode[job.jobId]" class="btn btn-dark me-1 btn-purrfect-primary" @click="toggleEditMode(job.jobId)">
                                Edit
                            </button>
                            <button v-else class="btn btn-dark me-1 btn-purrfect-primary" @click="applyChanges(job)">
                                Apply
                            </button>
                            <button v-if="editMode[job.jobId]" class="btn btn-dark me-1 btn-purrfect-primary" @click="exitEditing(job.jobId)">
                                Cancel
                            </button>
                            <button type="submit" class="btn btn-dark btn-purrfect-primary" id="btnCancel"
                                    @click="cancel(job.jobId)">
                                Delete
                            </button>
                        </td>
                    </tr>
                    <tr>
                        <td class="last-row" colspan="3">
                            <button v-if="appsize[job.jobId] > 0" class="btn btn-dark me-2 btn-purrfect-primary" @click="() => toggleApplicants(job.jobId)">
                                {{ showApplicants[job.jobId] ? 'Hide Applicants' : 'See Applicants' }}
                            </button>
                        </td>
                    </tr>
                    <tr v-if="showApplicants[job.jobId]">
                        <td colspan="4">
                            <!-- Iterate through applicants and display reject and approve buttons -->
                            <div v-for="applicant in applicants[job.jobId]" :key="applicant.id" class="d-flex justify-content-between align-items-center">
                                <div>
                                    <span>Name: {{ applicant.userBuster.firstName }} {{ applicant.userBuster.lastName }},</span>
                                    <span> Phone: {{ applicant.userBuster.phone }}</span>
                                </div>
                                <div>
                                    <button
                                        class="btn btn-dark me-2 btn-purrfect-primary"
                                        :disabled="applicant.status === 'Rejected'"
                                        @click="rejectApplicant(applicant)"
                                    >
                                        {{ applicant.status === 'Rejected' ? 'Rejected' : 'Reject' }}
                                    </button>
                                    <button
                                        class="btn btn-dark btn-purrfect-primary"
                                        :disabled="applicant.status === 'Approved'"
                                        @click="approveApplicant(applicant)"
                                    >
                                        {{ applicant.status === 'Approved' ? 'Approved' : 'Approve' }}
                                    </button>
                                </div>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div class="wrapperPostedGigs d-flex flex-column align-items-center" v-else>
            <h5>You haven't posted any gigs yet.</h5>
        </div>

    </div>
</template>
<script>
import PostedGigService from "../services/PostedGigService";
import ApplicationService from "../services/ApplicationService";
import { reactive } from 'vue';
export default {
    name: "listPostedGigs",
    data() {
        return {
            currentUser: null,
            postedGigsResponse: {},
            appsize: {},
            deleteJobResponse: {},
            showApplicants: {}, // To track whether to show applicants for each job
            applicants: {}, // To store the list of applicants for each job
            editMode: {},
            message: "",
        }
    },
    methods: {
        getDaysAgo(date) {
            const today = new Date();
            const postingDate = new Date(date);
            const diffTime = today.getTime() - postingDate.getTime();
            const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
            if (diffDays === 0) {
                return "today";
            } else if (diffDays === 1) {
                return "yesterday";
            } else {
                return `${diffDays} days ago`;
            }
        },
        getCurrentUser() {
            this.currentUser = JSON.parse(localStorage.getItem('currentUser'));
            if (this.currentUser != null) {
                this.disabled = "";
            }
            console.log(this.currentUser);
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
        toggleEditMode(jobId) {
            // Create a reactive object for editMode if it doesn't exist
            if (!this.editMode) {
                this.editMode = reactive({});
            }
            // Find the job with jobId in the postedGigsResponse array
            const job = this.postedGigsResponse.find(job => job.jobId === jobId);

            // Toggle the edit mode only if the job is found
            if (job) {
                // Store initial values before entering edit mode
                job.editImage = job.image;
                job.editStartDate = job.jobStartDate;
                job.editFinishDate = job.jobFinishDate;
                job.editStartTime = job.startTime;
                job.editEndTime = job.endTime;
                // Use the reactive object to toggle the edit mode
                this.editMode[jobId] = true;
            }
            // Use the reactive object to toggle the edit mode
            this.editMode[jobId] = true;

        },
        applyDateTimeChanges(job) {
            job.image = job.editImage;
            job.jobStartDate = job.editStartDate;
            job.jobFinishDate = job.editFinishDate;
            job.startTime = job.editStartTime;
            job.endTime = job.editEndTime;

            // Reset the edit properties
            job.editImage = '';
            job.editStartDate = '';
            job.editFinishDate = '';
            job.editStartTime = '';
            job.editEndTime = '';
        },
        // Add a method to update the job field during inline editing
        updateJobField(job, field, event) {  
            if (!job) {
                console.error('Job is undefined.');
                return;
            } 
            if(field === 'image') {
                const file = event.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    reader.onload = (e) => {
                        job[field] = e.target.result.split(",")[1]; // extract Base64 data
                    };
                    reader.readAsDataURL(file);
                    console.log(file);

                }
            } else if (field === 'jobStartDate' || field === 'jobFinishDate' || field === 'startTime' || field === 'endTime') {
                // Handle date fields
                job[field] = event.target.value;
            } else {
                // Handle other fields
                job[field] = event.target.textContent;
            }
        },
        // Additional method to exit editing mode
        exitEditing(jobId) {
            // Find the job with jobId in the postedGigsResponse array
            const job = this.postedGigsResponse.find(job => job.jobId === jobId);
            // Restore original values before exiting edit mode
            job.image = job.editImage;
            job.jobStartDate = job.editStartDate;
            job.jobFinishDate = job.editFinishDate;
            job.startTime = job.editStartTime;
            job.endTime = job.editEndTime;

            // Use the reactive object to toggle the edit mode
            this.editMode[jobId] = false;
        },
        applyChanges(job) {
            // Call the service method to update the job
            PostedGigService.editJob(job, job.jobId)
                .then((response) => {
                    console.log("Job updated successfully:", response.data);
                    console.error("Updated job:", job);
                    this.editMode[job.jobId] = false; // Disable edit mode after applying changes
                })
                .catch((error) => {
                    console.error("Error updating job:", error);
                });
        },
        cancel(jobId) {
            console.log(typeof jobId);
            PostedGigService.cancelJob(jobId)
                .then(response => {
                    this.deleteJobResponse = response;
                    console.log("Deleted job ");
                    this.$router.go(0);
                })
                .catch(error => {
                    console.log(error);
                })
        },
        toggleApplicants(jobId) {
        // Create a reactive object for showApplicants if it doesn't exist
        if (!this.showApplicants) {
        this.showApplicants = reactive({});
        }

        // Use the reactive object to toggle the showApplicants flag
        this.showApplicants[jobId] = !this.showApplicants[jobId];
        // If applicants are not already fetched, fetch them
        if (!this.applicants[jobId]) {
                PostedGigService.getApplicationsByJobId(jobId)
                    .then((applicationsResponse) => {
                        const applicantsArray = Array.isArray(applicationsResponse.data)
                            ? applicationsResponse.data
                            : [applicationsResponse.data];

                        this.applicants[jobId] = applicantsArray;
                        console.log("Applicants:", this.applicants[jobId]);

                        // Check if userBuster exists in the data structure
                        if (this.applicants[jobId].length > 0 && this.applicants[jobId][0].userBuster) {
                            // Now you can access the userBuster's properties like firstName, lastName, and phone
                            console.log("First Name:", this.applicants[jobId][0].userBuster.firstName);
                            console.log("Last Name:", this.applicants[jobId][0].userBuster.lastName);
                            console.log("Phone:", this.applicants[jobId][0].userBuster.phone);
                        }
                    })
                    .catch((error) => {
                        console.log(error);
                    });
    }
        },
        rejectApplicant(applicant) {
            if (applicant.status !== 'Rejected') {
                applicant.status = 'Rejected';
                // Call the service method to update the status
                ApplicationService.updateApplicationStatus(applicant.applicationId, 'Rejected');
            }
        },
        approveApplicant(applicant) {
            if (applicant.status !== 'Approved') {
                applicant.status = 'Approved';
                // Call the service method to update the status
                ApplicationService.updateApplicationStatus(applicant.applicationId, 'Approved');
            }
        },
    },
    created() {
        //  const userId = 1;
        // const userId = this.getCurrentUser().id;
        this.getCurrentUser();
        PostedGigService.getJobsByUserId(this.currentUser.userId)
            .then((response) => {
                const postedGigs = response.data;
                const promises = postedGigs.map(job => {
                    return PostedGigService.getApplicationsByJobId(job.jobId)
                        .then((applicationsResponse) => {
                            this.appsize[job.jobId] = applicationsResponse.data.length;
                        })
                        .catch((error) => {
                            console.log(error);
                        });
                });
                Promise.all(promises).then(() => {
                    this.postedGigsResponse = postedGigs.reverse();
                });
            })
            .catch((error) => {
                console.log(error);
            });
    },
}
</script>


<style>
.wrapperPostedGigs h5 {
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

.wrapperPostedGigs .badge {
    background-color: #7c24c4;
}
.job-image {
    width: 80px; /* Set your preferred width */
  height: auto; /* Maintain aspect ratio */
}

</style>