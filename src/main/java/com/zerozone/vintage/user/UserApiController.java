package com.zerozone.vintage.user;

import com.zerozone.vintage.account.AccountService;
import com.zerozone.vintage.account.AuthenticationManager;
import com.zerozone.vintage.account.CheckedUser;
import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.tag.CameraTag;
import com.zerozone.vintage.tag.CameraTagForm;
import com.zerozone.vintage.tag.LocationTag;
import com.zerozone.vintage.dto.CustomResDto;
import com.zerozone.vintage.exception.CustomException;
import com.zerozone.vintage.tag.CameraTagRepository;
import com.zerozone.vintage.tag.LocationTagForm;
import com.zerozone.vintage.tag.LocationTagRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Transactional
public class UserApiController {

    private final AccountService accountService;
    private final NicknameValidator nicknameValidator;
    private final CameraTagRepository cameraTagRepository;
    private final PasswrodFormValidator passwrodFormValidator;
    private final LocationTagRepository locationTagRepository;

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder){
        webDataBinder.addValidators(passwrodFormValidator);
    }

    @InitBinder("nicknameForm")
    public void nicknameFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(nicknameValidator);
    }


    @PostMapping("/me/profile")
    @Operation(summary = "프로필 업데이트", description = "사용자의 프로필 정보를 업데이트.")
    @ApiResponse(responseCode = "200", description = "프로필 업데이트 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<Profile>> updateProfile(@CheckedUser Account account, @RequestBody @Valid Profile profile, BindingResult bindingResult) throws IOException {
        if(bindingResult.hasErrors()){
            throw new CustomException("프로필 업데이트 유효성 검사 실패", bindingResult);
        }

        Profile updatedProfile = accountService.updateProfile(account, profile);
        return ResponseEntity.ok(new CustomResDto<>(1, "프로필 업데이트에 성공.", updatedProfile));
    }

    @PutMapping("/me/password")
    @Operation(summary = "패스워드 업데이트", description = "사용자의 패스워드변경을 업데이트.")
    @ApiResponse(responseCode = "200", description = "패스워드 변경 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<String>> updatePassword(@CheckedUser Account account, @RequestBody @Valid PasswordForm passwordForm, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            throw new CustomException("패스워드 변경에 실패", bindingResult);
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        return ResponseEntity.ok(new CustomResDto<>(1, "패스워드 변경에 성공.", "성공"));
    }


    @PostMapping("/me/nickname")
    @Operation(summary = "닉네임 업데이트", description = "사용자의 닉네임변경을 업데이트.")
    @ApiResponse(responseCode = "200", description = "닉네임 변경 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<String>> updateNickname(@CheckedUser Account account, @RequestBody @Valid NicknameForm nicknameForm, BindingResult bindingResult,
                                                             HttpServletRequest request, HttpServletResponse response) {
        if(bindingResult.hasErrors()){
            throw new CustomException("닉네임 변경에 실패", bindingResult);
        }

        accountService.updateNickname(account, nicknameForm.getNickName());
        AuthenticationManager.login(account, request, response);
        return ResponseEntity.ok(new CustomResDto<>(1, "닉네임 변경에 성공.", "성공"));
    }


    @GetMapping("/me/camera-tags")
    @Operation(summary = "관심카메라 태그 조회", description = "사용자의 관심카메라 태그 조회")
    @ApiResponse(responseCode = "200", description = "관심카메라 태그 조회 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<List<String>>> getCameraTags(@CheckedUser Account account) {
        Set<CameraTag> cameraTags = accountService.getCameraTags(account);
        List<String> tagTitles = cameraTags.stream().map(CameraTag::getTitle).collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResDto<>(1, "관심 카메라 태그 조회 성공", tagTitles));
    }

    @PostMapping("/me/camera-tags")
    @Operation(summary = "관심 카메라 해시태그 추가", description = "사용자의 관심 카메라 해시태그 추가.")
    @ApiResponse(responseCode = "200", description = "관심 카메라 해시태그 등록 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<String>> addCameraTags(@CheckedUser Account account, @RequestBody CameraTagForm cameratagForm) {
        String title = cameratagForm.getTitle();
        CameraTag cameraTag = cameraTagRepository.findByTitle(title).orElseGet(() -> cameraTagRepository.save(CameraTag.builder()
                        .title(cameratagForm.getTitle())
                        .build()));


        accountService.addCameraTag(account, cameraTag);
        return ResponseEntity.ok(new CustomResDto<>(1, "관심카메라 등록에 성공.", "성공"));
    }

    @DeleteMapping("/me/camera-tags")
    @Operation(summary = "관심카메라 태그 삭제", description = "사용자의 관심카메라 태그 삭제")
    @ApiResponse(responseCode = "200", description = "관심카메라 태그 삭제 성공", content = @Content(schema = @Schema(implementation = CustomResDto.class)))
    public ResponseEntity<CustomResDto<String>> removeCameraTags(@CheckedUser Account account, @RequestBody CameraTagForm cameraTagForm) {
        String title = cameraTagForm.getTitle();

        CameraTag cameraTag = cameraTagRepository.findByTitle(title)
                             .orElseThrow(() -> new CustomException("태그를 삭제할 수 없습니다."));

        accountService.removeCameraTags(account, cameraTag);

        return ResponseEntity.ok(new CustomResDto<>(1, "관심카메라 삭제에 성공.", "성공"));
    }


    @GetMapping("/me/location-tags")
    public ResponseEntity<CustomResDto<List<String>>> getCameraLocationTags(@CheckedUser Account account) {
        Set<LocationTag> locationTags = accountService.getLocationTags(account);
        List<String> tagTitles = locationTags.stream().map(LocationTag::getTitle).collect(Collectors.toList());
        return ResponseEntity.ok(new CustomResDto<>(1, "활동 지역 태그 조회 성공", tagTitles));
    }

    @PostMapping("/me/location-tags")
    public ResponseEntity<CustomResDto<String>> addCameraLocationTag(@CheckedUser Account account, @RequestBody LocationTagForm locationTagForm) {
        String title = locationTagForm.getTitle();
        LocationTag locationTag = locationTagRepository.findByTitle(title).orElseGet(() -> locationTagRepository.save(LocationTag.builder()
                .title(locationTagForm.getTitle())
                .build()));


        accountService.addLocationTag(account, locationTag);
        return ResponseEntity.ok(new CustomResDto<>(1, "활동 지역 태그 등록에 성공.", "성공"));
    }

    @DeleteMapping("/me/location-tags")
    public ResponseEntity<CustomResDto<String>> removeCameraLocationTag(@CheckedUser Account account, @RequestBody LocationTagForm locationTagForm) {
        String title = locationTagForm.getTitle();

        LocationTag locationTag = locationTagRepository.findByTitle(title)
                .orElseThrow(() -> new CustomException("태그를 삭제할 수 없습니다."));

        accountService.removeLocationTag(account, locationTag);

        return ResponseEntity.ok(new CustomResDto<>(1, "활동 지역 태그 삭제에 성공.", "성공"));
    }








}
