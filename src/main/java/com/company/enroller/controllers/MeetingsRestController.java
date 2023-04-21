package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Column;
import java.util.Collection;

@RestController
@RequestMapping("/meetings")
public class MeetingsRestController {

	@Autowired
	MeetingService meetingsService;
	@Autowired
	ParticipantService participantService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetings() {

		Collection<Meeting> meetings;
		meetings = meetingsService.getAll();
		return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
		if (meetingsService.alreadyExist(meeting)) {
			return new ResponseEntity<String>(
					"Unable to create. A meeting with id " + meeting.getId() + " already exist.",
					HttpStatus.CONFLICT);
		}

		meetingsService.add(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/{meeting_id}", method = RequestMethod.GET)
	public ResponseEntity<?> getMeeting(@PathVariable("meeting_id") long id) {
		Meeting meeting = meetingsService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}

	@RequestMapping(value = "/{meeting_id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("meeting_id") long id) {
		Meeting meeting = meetingsService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		meetingsService.delete(meeting);
		return new ResponseEntity<Participant>(HttpStatus.OK);
	}

	@RequestMapping(value = "/{meeting_id}", method = RequestMethod.PUT)
	public ResponseEntity<?> updateMeeting(
			@PathVariable("meeting_id") long id,
			@RequestBody Meeting updatedMeeting) {
		Meeting meeting = meetingsService.findById(id);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		meeting.setTitle(updatedMeeting.getTitle());
		meeting.setDescription(updatedMeeting.getDescription());
		meeting.setDate(updatedMeeting.getDate());
		meetingsService.update(meeting);
		return new ResponseEntity<Participant>(HttpStatus.OK);
	}

	@RequestMapping(value = "/{meeting_id}/participants", method = RequestMethod.GET)
	public ResponseEntity<?> getMeetingParticipants(@PathVariable("meeting_id") long id) {
		Meeting meeting = meetingsService.findById(id);
		if (meeting == null) {
			return new ResponseEntity<String>(
					"Provided meeting_id does not exist.",
					HttpStatus.NOT_FOUND);
		}
		Collection<Participant> participants = meeting.getParticipants();
		return new ResponseEntity<Collection<Participant>>(participants, HttpStatus.OK);
	}

	@RequestMapping(value = "/{meeting_id}/participants/{participant_id}", method = RequestMethod.POST)
	public ResponseEntity<?> addMeetingParticipant(
			@PathVariable("meeting_id") long id,
			@PathVariable("participant_id") String login) {
		Meeting meeting = meetingsService.findById(id);
		Participant participant = participantService.findByLogin(login);
		if (meeting == null || participant == null) {
			return new ResponseEntity<String>(
					"Unable to assign participant to meeting. Provided meeting_id or participant_id does not exist.",
					HttpStatus.NOT_FOUND);
		} else if (meeting.getParticipants().contains(participant)) {
			return new ResponseEntity<String>(
					"Provided participant_id already assigned to given meeting_id.",
					HttpStatus.CONFLICT);
		}
		meeting.addParticipant(participant);
		meetingsService.update(meeting);
		return new ResponseEntity<String>(
				"Participant assigned to meeting",
				HttpStatus.OK);
	}

	@RequestMapping(value = "/{meeting_id}/participants/{participant_id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> removeMeetingParticipant(
			@PathVariable("meeting_id") long id,
			@PathVariable("participant_id") String login) {
		Meeting meeting = meetingsService.findById(id);
		Participant participant = participantService.findByLogin(login);
		if (meeting == null || participant == null) {
			return new ResponseEntity<String>(
					"Unable to assign participant to meeting. Provided meeting_id or participant_id does not exist.",
					HttpStatus.NOT_FOUND);
		} else if (!meeting.getParticipants().contains(participant)) {
			return new ResponseEntity<String>(
					"Provided participant_id is not assigned to given meeting_id.",
					HttpStatus.NOT_FOUND);
		}
		meeting.removeParticipant(participant);
		meetingsService.update(meeting);
		return new ResponseEntity<String>(
				"Participant unassigned from meeting",
				HttpStatus.OK);
	}

}
