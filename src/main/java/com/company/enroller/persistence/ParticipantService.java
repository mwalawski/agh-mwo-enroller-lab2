package com.company.enroller.persistence;

import com.company.enroller.App;
import com.company.enroller.model.Participant;

import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("participantService")
public class ParticipantService {

	DatabaseConnector connector;

	@Autowired
	App app;

	public ParticipantService() {
		connector = DatabaseConnector.getInstance();
	}

	public Collection<Participant> getAll() {
		return connector.getSession().createCriteria(Participant.class).list();
	}

	public Collection<Participant> getAllSorted(String sortBy, String sortOrder, String key) {
		String hql = "FROM Participant as p";
		hql += " WHERE p.login LIKE '%" + key + "%'";
		if (sortBy.equals("login")) {
			hql += " ORDER BY p." + sortBy;
			if (sortOrder.equals("ASC") || sortOrder.equals("DESC")) {
				hql += " " + sortOrder;
			}
		}
		Query query = connector.getSession().createQuery(hql);
		return query.list();
	}

	public Participant findByLogin(String login) {
		return connector.getSession().get(Participant.class, login);
	}

	public Participant add(Participant participant) {
		String hashedPassword = app.passwordEncoder().encode(participant.getPassword());
		participant.setPassword(hashedPassword);

		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(participant);
		transaction.commit();
		return participant;
	}

	public void update(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().merge(participant);
		transaction.commit();
	}

	public void delete(Participant participant) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(participant);
		transaction.commit();
	}

}
