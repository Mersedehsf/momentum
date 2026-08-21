package org.momentum.dto;


public class CategoryDTO extends BaseDTO {

    private String title;

    private Integer estimatedMinutes;

    public CategoryDTO(Long id,String title,Integer estimatedMinutes) {
        super(id);
        this.title = title;
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }
}
