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
        meeting.setStatus(MeetingStatus.NOT_STARTED);  // 우선 임시로 초기 상태는 NOT_STARTED
        return meetingRepository.save(meeting);
    }

    public Meeting updateMeeting(Long id, Meeting meetingDetails, Account organizer) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new CustomException("모임을 찾을 수 없습니다."));

        if (!meeting.getOrganizer().equals(organizer)) {
            throw new CustomException("수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        modelMapper.map(meetingDetails, meeting);

        return meetingRepository.save(meeting);
    }

    public void deleteMeeting(Long id, Account organizer) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new CustomException("모임을 찾을 수 없습니다."));

        if (!meeting.getOrganizer().equals(organizer)) {
            throw new CustomException("삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        meetingRepository.delete(meeting);
    }

    public List<Meeting> getAllMeetings() {
        return meetingRepository.findAll();
    }

    public void joinMeeting(Long id, Account account) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new CustomException("모임을 찾을 수 없습니다."));
        meeting.getParticipants().add(account);
        meetingRepository.save(meeting);
    }

    public void updateMeetingStatus(Long id, MeetingStatus status) {
        Meeting meeting = meetingRepository.findById(id)
                .orElseThrow(() -> new CustomException("모임을 찾을 수 없습니다."));
        meeting.setStatus(status);
        meetingRepository.save(meeting);
    }

    public Page<Meeting> searchMeetings(String keyword, String searchType, Pageable pageable) {
        if ("title".equalsIgnoreCase(searchType)) {
            return meetingRepository.findByTitle(keyword, pageable);
        } else if ("description".equalsIgnoreCase(searchType)) {
            return meetingRepository.findByDescription(keyword, pageable);
        } else {
            return meetingRepository.findByTitleOrDescription(keyword, keyword, pageable);
        }
    }
}