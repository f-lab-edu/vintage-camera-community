package com.zerozone.vintage.meeting;

import com.zerozone.vintage.account.Account;
import com.zerozone.vintage.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final ModelMapper modelMapper;

    public Meeting createMeeting(Meeting meeting, Account organizer) {
        meeting.setOrganizer(organizer);
        meeting.setStatus(MeetingStatus.NOT_STARTED);
        return meetingRepository.save(meeting);
    }

    public Meeting updateMeeting(Long id, Meeting meetingDetails, Account organizer) {
        Meeting meeting = findMeeting(id);
        checkUpdatePermission(meeting, organizer);
        modelMapper.map(meetingDetails, meeting);
        return meetingRepository.save(meeting);
    }

    public void deleteMeeting(Long id, Account organizer) {
        Meeting meeting = findMeeting(id);
        checkDeletePermission(meeting, organizer);
        meetingRepository.delete(meeting);
    }

    public Page<Meeting> getAllMeetings(Pageable pageable) {
        return meetingRepository.findAll(pageable);
    }

    public void joinMeeting(Long id, Account account) {
        Meeting meeting = findMeeting(id);
        meeting.getParticipants().add(account);
        meetingRepository.save(meeting);
    }

    public void updateMeetingStatus(Long id, MeetingStatus status) {
        Meeting meeting = findMeeting(id);
        meeting.setStatus(status);
        meetingRepository.save(meeting);
    }

    public Page<Meeting> searchMeetings(String keyword, SearchType searchType, Pageable pageable) {
        if (searchType == SearchType.TITLE) {
            return meetingRepository.findByTitleContaining(keyword, pageable);
        } else if (searchType == SearchType.DESCRIPTION) {
            return meetingRepository.findByDescriptionContaining(keyword, pageable);
        } else {
            return meetingRepository.findByTitleContainingOrDescriptionContaining(keyword, keyword, pageable);
        }
    }

    private Meeting findMeeting(Long id) {
        return meetingRepository.findById(id)
                .orElseThrow(() -> new CustomException("모임을 찾을 수 없습니다."));
    }

    private void checkUpdatePermission(Meeting meeting, Account organizer) {
        if (!meeting.getOrganizer().equals(organizer)) {
            throw new CustomException("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }

    private void checkDeletePermission(Meeting meeting, Account organizer) {
        if (!meeting.getOrganizer().equals(organizer)) {
            throw new CustomException("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }
    }



}