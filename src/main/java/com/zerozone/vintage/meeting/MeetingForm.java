package com.zerozone.vintage.meeting;

import com.zerozone.vintage.tag.CameraTag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Data;

@Data
public class MeetingForm {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotNull
    private LocalDateTime startDateTime;

    @NotNull
    private LocalDateTime endDateTime;

    private Set<CameraTag> tags;

    private boolean isPublic;
}