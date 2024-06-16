package com.zerozone.vintage.meeting;

import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.dto.CustomResDto;
import com.zerozone.vintage.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/meetings")
@RequiredArgsConstructor
@Transactional
public class MeetingApiController {

    private final MeetingService meetingService;
    private final ModelMapper modelMapper;

    @PostMapping
    @Operation(summary = "모임 등록", description = "모임 게시글 등록")
    @ApiResponse(responseCode = "200", description = "모임 게시글 등록 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Meeting>> createMeeting(@CheckedUser Account account, @RequestBody @Valid MeetingForm meetingForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException("모임 등록 유효성 검사 실패", bindingResult);
        }
        Meeting meeting = modelMapper.map(meetingForm, Meeting.class);
        Meeting newMeeting = meetingService.createMeeting(meeting, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "모임 등록에 성공했습니다.", newMeeting));
    }

    @PutMapping("/{id}")
    @Operation(summary = "모임 수정", description = "모임 게시글 수정")
    @ApiResponse(responseCode = "200", description = "모임 게시글 수정 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Meeting>> updateMeeting(@CheckedUser Account account, @PathVariable Long id, @RequestBody @Valid MeetingForm meetingForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new CustomException("모임 수정 유효성 검사 실패", bindingResult);
        }
        Meeting meeting = modelMapper.map(meetingForm, Meeting.class);
        Meeting updatedMeeting = meetingService.updateMeeting(id, meeting, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "모임 수정에 성공했습니다.", updatedMeeting));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "모임 삭제", description = "모임 게시글 삭제")
    @ApiResponse(responseCode = "200", description = "모임 게시글 삭제 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Void>> deleteMeeting(@CheckedUser Account account, @PathVariable Long id) {
        meetingService.deleteMeeting(id, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "모임이 삭제되었습니다.", null));
    }

    @GetMapping
    @Operation(summary = "모임 조회", description = "모임 게시글 목록 조회")
    @ApiResponse(responseCode = "200", description = "모임 게시글 목록 조회 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<List<Meeting>>> getAllMeetings() {
        List<Meeting> meetings = meetingService.getAllMeetings();
        return ResponseEntity.ok(new CustomResDto<>(1, "모임 조회 성공.", meetings));
    }

    @PostMapping("/{id}/join")
    @Operation(summary = "모임 참여", description = "모임 게시글 참여")
    @ApiResponse(responseCode = "200", description = "모임 게시글 참여 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Void>> joinMeeting(@CheckedUser Account account, @PathVariable Long id) {
        meetingService.joinMeeting(id, account);
        return ResponseEntity.ok(new CustomResDto<>(1, "모임에 참여했습니다.", null));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "모임 상태 변경", description = "모임 게시글 상태 변경")
    @ApiResponse(responseCode = "200", description = "모임 게시글 상태 변경 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Void>> updateMeetingStatus(@PathVariable Long id, @RequestParam MeetingStatus status) {
        meetingService.updateMeetingStatus(id, status);
        return ResponseEntity.ok(new CustomResDto<>(1, "모임 상태가 업데이트 되었습니다.", null));
    }

    @GetMapping("/search")
    @Operation(summary = "모임 게시글 검색", description = "모암 게시글 조건 검색")
    @ApiResponse(responseCode = "200", description = "모임 게시글 검색 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<org.springframework.data.domain.Page<Meeting>>> searchMeetings(@RequestParam String keyword, @RequestParam String searchType, @PageableDefault(size = 10) Pageable pageable) {
        Page<Meeting> meetings = meetingService.searchMeetings(keyword, searchType, pageable);
        return ResponseEntity.ok(new CustomResDto<>(1, "검색 결과입니다.", meetings));
    }
}