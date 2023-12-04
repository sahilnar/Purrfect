package com.purrfect.app.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Lob;

@Entity
@Table(name = "job")
public class Job {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long jobId;

	@Column(name = "title")
	private String title;

	@Column(name = "posting_date")
	@Temporal(TemporalType.DATE)
	private LocalDate postingDate; // Usage example LocalDate.of(2022, 2, 14)

	@Column(name = "hour_rate")
	private double hourRate;

	@Column(name = "kind_of_job")
	private String kindOfJob;

	@Column(name = "description")
	private String description;

	@Column(name = "job_start_date")
	@Temporal(TemporalType.DATE)
	private LocalDate jobStartDate; // Usage example LocalDate.of(2022, 2, 14)

	@Column(name = "job_finish_date")
	@Temporal(TemporalType.DATE)
	private LocalDate jobFinishDate; // Usage example LocalDate.of(2022, 2, 14)

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	@JsonIgnore
	private User user;
	
    @Column(name = "image_path")
    private String imagePath;
	
    @Lob
    @Column(name = "image", columnDefinition="BLOB")
    private byte[] image;


	@OneToMany(mappedBy = "job", // in Application class field must be defined private Job job;
			cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	@JsonIgnore
	private Set<Application> applications = new HashSet<>();

	// Contructors and getters, setters
	public Job() {
	}

	public Job(User user, String title, LocalDate postingDate, double hourRate, String kindOfJob,
			String description, LocalDate jobStartDate, LocalDate jobFinishDate, String imageName) {
		this.user = user;
		this.title = title;
		this.postingDate = postingDate;
		this.hourRate = hourRate;
		this.kindOfJob = kindOfJob;
		this.description = description;
		this.jobStartDate = jobStartDate;
		this.jobFinishDate = jobFinishDate;
	    if (imageName != null && !imageName.isEmpty()) {
	        this.image = loadImageData(imageName);
	    }
		// user.getUserPostings().add(this);
	}

	public Job(String title, LocalDate postingDate, double hourRate, String kindOfJob,
			String description, LocalDate jobStartDate, LocalDate jobFinishDate, String imageName) {
		this.title = title;
		this.postingDate = postingDate;
		this.hourRate = hourRate;
		this.kindOfJob = kindOfJob;
		this.description = description;
		this.jobStartDate = jobStartDate;
		this.jobFinishDate = jobFinishDate;
	    if (imageName != null && !imageName.isEmpty()) {
	        this.image = loadImageData(imageName);
	    }
		// user.getUserPostings().add(this);
	}
	
    // Constructor with image data
    public Job(User user, String title, LocalDate postingDate, double hourRate, String kindOfJob,
            String description, LocalDate jobStartDate, LocalDate jobFinishDate, byte[] image) {
        this.user = user;
        this.title = title;
        this.postingDate = postingDate;
        this.hourRate = hourRate;
        this.kindOfJob = kindOfJob;
        this.description = description;
        this.jobStartDate = jobStartDate;
        this.jobFinishDate = jobFinishDate;
        this.image = image;
    }

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public User getUserPoster() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDate getPostingDate() {
		return postingDate;
	}

	public void setPostingDate(LocalDate postingDate) {
		this.postingDate = postingDate;
	}

	public double getHourRate() {
		return hourRate;
	}

	public void setHourRate(double hourRate) {
		this.hourRate = hourRate;
	}

	public String getKindOfJob() {
		return kindOfJob;
	}

	public void setKindOfJob(String kindOfJob) {
		this.kindOfJob = kindOfJob;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalDate getJobStartDate() {
		return jobStartDate;
	}

	public void setJobStartDate(LocalDate jobStartDate) {
		this.jobStartDate = jobStartDate;
	}

	public LocalDate getJobFinishDate() {
		return jobFinishDate;
	}

	public void setJobFinishDate(LocalDate jobFinishDate) {
		this.jobFinishDate = jobFinishDate;
	}

	public void addApplication(Application application) {
		this.applications.add(application);
		application.setJob(this);
	}

	public Set<Application> getApplications() {
		return applications;
	}

	public void setApplications(Set<Application> applications) {
		this.applications = applications;
	}
	
	// Getter for image file path
	public String getImagePath() {
	    return imagePath;
	}

	// Setter for image file path
	public void setImagePath(String imagePath) {
	    this.imagePath = imagePath;
	}

	// Getter for image data
	public byte[] getImage() {
	    return image;
	}

	// Setter for image data
	public void setImage(byte[] image) {
	    this.image = image;
	}

    
    public byte[] loadImageData(String imagePath) {
        try {
            // Load image data from the specified absolute path
            Path path = Paths.get(imagePath);
            InputStream imageStream = Files.newInputStream(path);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Copy the InputStream to the ByteArrayOutputStream without Apache Commons IO
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = imageStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
