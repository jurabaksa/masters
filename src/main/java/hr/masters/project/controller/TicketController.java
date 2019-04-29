package hr.masters.project.controller;

import hr.masters.project.facades.MatchFacade;
import hr.masters.project.facades.TicketFacade;
import hr.masters.project.facades.UserFacade;
import hr.masters.project.forms.NewMatchForm;
import hr.masters.project.forms.NewTicketForm;
import hr.masters.project.util.Constants;
import hr.masters.project.validators.TicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TicketController
{
    private static final String TICKETS_ATTRIBUTE = "tickets";
    private static final String NEW_TICKET_ATTRIBUTE = "ticket";
    private static final String USER_ATTRIBUTE = "user";
    private static final String MATCHES_ATTRIBUTE = "matches";
    private static final String NEW_MATCH_ATTRIBUTE = "match";

    @Autowired
    private TicketValidator ticketValidator;

    @Autowired
    private TicketFacade ticketFacade;

    @Autowired
    private UserFacade userFacade;

    @Autowired
    private MatchFacade matchFacade;

    @RequestMapping(value = Constants.Paths.MY_TICKETS, method = RequestMethod.GET)
    public ModelAndView showMyTickets()
    {
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModelMap().addAttribute(TICKETS_ATTRIBUTE, ticketFacade.retrieveUserTickets());
        modelAndView.getModelMap().addAttribute(USER_ATTRIBUTE, userFacade.retrieveLoggedUser().getUsername());
        modelAndView.setViewName(Constants.Pages.MY_TICKETS);
        return modelAndView;
    }

    @RequestMapping(value = Constants.Paths.ADD_TICKET, method = RequestMethod.GET)
    public ModelAndView showAddTicketForm()
    {
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(Constants.Pages.ADD_TICKET);
        modelAndView.getModelMap().addAttribute(NEW_TICKET_ATTRIBUTE, new NewTicketForm());
        modelAndView.getModelMap().addAttribute(USER_ATTRIBUTE, userFacade.retrieveLoggedUser().getUsername());
        return modelAndView;
    }

    @RequestMapping(value = Constants.Paths.ADD_TICKET, method = RequestMethod.POST)
    public ModelAndView addTicket(
            @ModelAttribute(NEW_TICKET_ATTRIBUTE)
            final NewTicketForm newTicketForm,
            final BindingResult bindingResult)
    {
        final ModelAndView modelAndView = new ModelAndView();
        ValidationUtils.invokeValidator(ticketValidator, newTicketForm, bindingResult);

        if (bindingResult.hasErrors())
        {
            modelAndView.getModelMap().addAttribute(NEW_TICKET_ATTRIBUTE, newTicketForm);
            modelAndView.getModelMap().addAttribute(USER_ATTRIBUTE, userFacade.retrieveLoggedUser().getUsername());
            modelAndView.setViewName(Constants.Pages.ADD_TICKET);

        }
        else
        {
            ticketFacade.createNewTicket(newTicketForm);
            modelAndView.getModelMap().addAttribute(USER_ATTRIBUTE, userFacade.retrieveLoggedUser().getUsername());
            modelAndView.setViewName(Constants.Pages.HOME);
        }
        return modelAndView;
    }

    @RequestMapping(value = Constants.Paths.TICKET_DETAILS, method = RequestMethod.GET)
    public ModelAndView retrieveTicketDetailsRedirect()
    {
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModelMap().addAttribute(USER_ATTRIBUTE, userFacade.retrieveLoggedUser().getUsername());
        modelAndView.setViewName(Constants.Pages.HOME);
        return modelAndView;
    }

    @RequestMapping(value = Constants.Paths.TICKET_DETAILS, method = RequestMethod.POST)
    public ModelAndView retrieveTicketDetails(
            @RequestParam
            final String ticket)
    {
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModelMap().addAttribute(USER_ATTRIBUTE, userFacade.retrieveLoggedUser().getUsername());
        modelAndView.getModelMap().addAttribute(NEW_MATCH_ATTRIBUTE, new NewMatchForm());

        final NewTicketForm currentTicket = getCurrentTicket(ticket);
        modelAndView.getModelMap().addAttribute(NEW_TICKET_ATTRIBUTE, currentTicket);
        modelAndView.getModelMap().addAttribute(MATCHES_ATTRIBUTE, matchFacade.retrieveTicketMatches(ticket));

        modelAndView.setViewName(Constants.Pages.TICKET_DETAILS);
        return modelAndView;
    }

    @RequestMapping(value = Constants.Paths.ADD_TICKET_DETAILS, method = RequestMethod.GET)
    public ModelAndView retrieveAddedMatchRedirect()
    {
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModelMap().addAttribute(USER_ATTRIBUTE, userFacade.retrieveLoggedUser().getUsername());
        modelAndView.setViewName(Constants.Pages.HOME);
        return modelAndView;
    }

    @RequestMapping(value = Constants.Paths.ADD_TICKET_DETAILS, method = RequestMethod.POST)
    public ModelAndView addNewMatchToTicket(
            @RequestParam
            final String ticket,
            @ModelAttribute(NEW_MATCH_ATTRIBUTE)
            final NewMatchForm newMatchForm, final BindingResult bindingResult)
    {
        final ModelAndView modelAndView = new ModelAndView();
        modelAndView.getModelMap().addAttribute(USER_ATTRIBUTE, userFacade.retrieveLoggedUser().getUsername());
        modelAndView.getModelMap().addAttribute(NEW_MATCH_ATTRIBUTE, new NewMatchForm());
        newMatchForm.setTicket(ticketFacade.retrieveTicket(ticket));
        matchFacade.addMatchToTicket(newMatchForm);

        modelAndView.getModelMap().addAttribute(MATCHES_ATTRIBUTE, matchFacade.retrieveTicketMatches(ticket));
        final NewTicketForm currentTicket = getCurrentTicket(ticket);
        modelAndView.getModelMap().addAttribute(NEW_TICKET_ATTRIBUTE, currentTicket);
        modelAndView.setViewName(Constants.Pages.TICKET_DETAILS);
        return modelAndView;
    }

    private NewTicketForm getCurrentTicket(final String ticket)
    {
        final NewTicketForm ticketForm = new NewTicketForm();
        ticketForm.setTicket_id(ticket);
        return ticketForm;
    }

}