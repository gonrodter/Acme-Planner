/*
 * EmployerJobUpdateService.java
 *
 * Copyright (C) 2012-2021 Rafael Corchuelo.
 *
 * In keeping with the traditional purpose of furthering education and research, it is
 * the policy of the copyright owner to permit non-commercial use and redistribution of
 * this software. It has been tested carefully, but it is not guaranteed for any particular
 * purposes. The copyright owner does not offer any warranties or representations, nor do
 * they accept any liabilities with respect to them.
 */

package acme.features.manager.task;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.customization.Customization;
import acme.entities.roles.Manager;
import acme.entities.tasks.Task;
import acme.framework.components.Errors;
import acme.framework.components.Model;
import acme.framework.components.Request;
import acme.framework.entities.Principal;
import acme.framework.services.AbstractUpdateService;

@Service
public class ManagerTaskUpdateService implements AbstractUpdateService<Manager, Task> {

	// Internal state ---------------------------------------------------------

	@Autowired
	protected ManagerTaskRepository repository;

	// AbstractListService<Employer, Job> -------------------------------------


	@Override
	public boolean authorise(final Request<Task> request) {
		assert request != null;

		boolean result;
		int taskId;
		Task task;
		Manager manager;
		Principal principal;

		taskId = request.getModel().getInteger("id");
		task = this.repository.findOneTaskById(taskId);
		manager = task.getManager();
		principal = request.getPrincipal();
		
		result = manager.getUserAccount().getId() == principal.getAccountId();
		
		
		
		return result;
	}
	@Override
	public void validate(final Request<Task> request, final Task entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		

		final List<Customization> repo= this.repository.findCustomization();
		
		String res;
		res= entity.getTitle().concat(" ").concat(entity.getDescription());
			
		final Integer taskID=entity.getId();
		final Task tareaConcreta= this.repository.findOneTaskById(taskID);
		
		
		
		final Date fechaInicial= tareaConcreta.getInitialMoment();
		final Date fechaFinal= tareaConcreta.getFinalMoment();
		
		final LocalDateTime fInicial= fechaInicial.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		final LocalDateTime fFinal= fechaFinal.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		if(fFinal.isBefore(LocalDateTime.now())) {
			errors.state(request, false, "finalMoment", "manager.task.create.error.label.finalMomentUpdate");
		}
		
		
		
		if(fInicial.isBefore(LocalDateTime.now()) && fFinal.isAfter(LocalDateTime.now())) {
			errors.state(request, false, "initialMoment", "manager.task.create.error.label.initialMomentUpdate");
		}
		
		
		
		if(ManagerTaskCreateService.esSpam(repo.get(0).getPalabrasSpam(), res, repo.get(0).getTolerancia())) {
			
			errors.state(request, false, "description", "manager.task.create.error.label.description");
		}
		
		if (!errors.hasErrors("finalMoment")) {
			final Date finalMom = entity.getFinalMoment();
			final Date initialMom = entity.getInitialMoment();
			
			errors.state(request, finalMom.compareTo(initialMom) > 0, "finalMoment", "manager.task.create.error.label.finalMomentBeforeInitialmoment");
		}
		
		if (!errors.hasErrors("workload")) {
			
			

			final String a= entity.getWorkload().toString();
			final int indexOfDecimal= a.indexOf(".");
			
			final Boolean dos= a.substring(indexOfDecimal+1).length()<=2;
						
			final Boolean enRangoDecimales= Double.parseDouble(a.substring(indexOfDecimal)) <.60 && Double.parseDouble(a.substring(indexOfDecimal))>= .0;
			
			final Boolean enRango=entity.getWorkload()>0 && entity.getWorkload()<100;
			
			
			errors.state(request, enRango&&enRangoDecimales&&dos, "workload", "manager.task.create.error.label.incorrectWorkload");
			errors.state(request, entity.getWorkload() > 0, "workload", "manager.task.create.error.label.negativeWorkload");
		}
		
		if (!errors.hasErrors("workload")) {
			final Date finalMom = entity.getFinalMoment();
			final Date initialMom = entity.getInitialMoment();
			
			final Long maxWorkload = finalMom.getTime() - initialMom.getTime();
			
			errors.state(request, entity.getWorkload()*3600000 <= maxWorkload, "workload", "manager.task.create.error.label.workload");
		}
		

		if(entity.getInitialMoment().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isBefore(LocalDateTime.now())) {
			errors.state(request, false, "initialMoment", "manager.task.create.error.label.initialMoment");
		}
		if(entity.getFinalMoment().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().isBefore(LocalDateTime.now())) {
			errors.state(request, false , "finalMoment", "manager.task.create.error.label.finalMoment");
		}
		
		
		
	}

	@Override
	public void bind(final Request<Task> request, final Task entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;

		request.bind(entity, errors);
	}

	@Override
	public void unbind(final Request<Task> request, final Task entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;

		request.unbind(entity, model, "title", "initialMoment", "finalMoment", "workload", "description", "isPublic");
	}

	@Override
	public Task findOne(final Request<Task> request) {
		assert request != null;

		Task result;
		int id;

		id = request.getModel().getInteger("id");
		result = this.repository.findOneTaskById(id);

		return result;
	}

	@Override
	public void update(final Request<Task> request, final Task entity) {
		assert request != null;
		assert entity != null;

		this.repository.save(entity);
	}

}
