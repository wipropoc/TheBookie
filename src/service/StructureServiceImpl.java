package service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import persistence.mybatis.mappers.StructureMapper;

import model.Booking;
import model.Extra;
import model.Facility;
import model.Room;
import model.RoomType;
import model.Structure;
import model.listini.Convention;
import model.listini.ExtraPriceList;
import model.listini.ExtraPriceListItem;
import model.listini.Period;
import model.listini.RoomPriceList;
import model.listini.RoomPriceListItem;
import model.listini.Season;

@Service
public class StructureServiceImpl implements StructureService{
	@Autowired
	private SeasonService seasonService = null;
	@Autowired
	private RoomPriceListService roomPriceListService = null;
	@Autowired
	private ExtraPriceListService extraPriceListService = null;
	@Autowired
	private RoomTypeService roomTypeService = null;
	@Autowired
	private BookingService bookingService = null;
	@Autowired
	private ExtraService extraService = null;
	@Autowired
	private ConventionService conventionService = null;
	@Autowired
	private StructureMapper structureMapper = null;
	@Autowired
	private RoomService roomService = null;
	@Autowired
	private GuestService guestService = null;
	@Autowired 
	private ImageService imageService = null;	
	@Autowired
	private FacilityService facilityService = null;
	
	@Override
	public Structure findStructureByIdUser(Integer id_user) {	
		Structure ret = null;
			
		ret = this.getStructureMapper().findStructureByIdUser(id_user);	
		
		return ret;
	}

	@Override
	public Structure findStructureById(Integer id) {
		
		return this.getStructureMapper().findStructureById(id);
	}

	@Override
	public List<Facility> findRoomFacilitiesByIdStructure(Structure structure) {
		
		return structure.getRoomFacilities();
	}

	@Override
	public Integer updateStructure(Structure structure) {
		
		return this.getStructureMapper().updateStructure(structure);
	}

	public Double calculateExtraItemUnitaryPrice(Structure structure, Date dateIn, Date dateOut, RoomType roomType, Convention convention, Extra extra) {
		Double ret = 0.0;
		ExtraPriceList priceList = null;
		ExtraPriceList otherPriceList = null;
		Season season = null;
		Season otherSeason = null;
		Double price = 0.0;
		Double otherPrice = 0.0;
		
		season = this.getSeasonService().findSeasonByDate(structure.getId(),dateIn );
		//priceList = structure.findExtraPriceListBySeasonAndRoomTypeAndConvention(season, roomType,convention);
		priceList = this.getExtraPriceListService().findExtraPriceListByStructureAndSeasonAndRoomTypeAndConvention(structure,season, roomType,convention);
		
		price = priceList.findExtraPrice(extra);
		
		//se ho un booking a cavallo di due stagioni, prendo il prezzo più basso
		otherSeason = this.getSeasonService().findSeasonByDate(structure.getId(),dateOut );
		//otherPriceList = structure.findExtraPriceListBySeasonAndRoomTypeAndConvention(otherSeason, roomType, convention);	
		otherPriceList = this.getExtraPriceListService().findExtraPriceListByStructureAndSeasonAndRoomTypeAndConvention(structure,season, roomType,convention);
		price = priceList.findExtraPrice(extra);
		otherPrice = otherPriceList.findExtraPrice(extra);
		
		ret = Math.min(price,otherPrice);
		
		return ret;
	}
	
	public void addPriceListsForSeason(Structure structure, Integer id_season) {
		RoomPriceList newRoomPriceList = null;
		ExtraPriceList newExtraPriceList = null;
		RoomPriceListItem newRoomPriceListItem = null;
		ExtraPriceListItem newExtraPriceListItem = null;
		Double price = 0.0;
		Season theNewSeason = null;

		theNewSeason = this.getSeasonService().findSeasonById(id_season);
		
		for (RoomType eachRoomType : this.getRoomTypeService().findRoomTypesByIdStructure(structure.getId())) {
			for (Convention eachConvention : this.getConventionService().findConventionsByIdStructure(structure.getId())) {
				//RoomPriceList
				newRoomPriceList = new RoomPriceList();
				newRoomPriceList.setId_structure(structure.getId());
				newRoomPriceList.setSeason(theNewSeason);
				newRoomPriceList.setId_season(theNewSeason.getId());
				newRoomPriceList.setRoomType(eachRoomType);
				newRoomPriceList.setId_roomType(eachRoomType.getId());
				newRoomPriceList.setConvention(eachConvention);
				newRoomPriceList.setId_convention(eachConvention.getId());
				List<RoomPriceListItem> roomItems = new ArrayList<RoomPriceListItem>();
				for (int i = 1; i <= eachRoomType.getMaxGuests(); i++) {
					newRoomPriceListItem = new RoomPriceListItem();
					newRoomPriceListItem.setNumGuests(i);
					newRoomPriceListItem.setPriceMonday(0.0);// lun
					newRoomPriceListItem.setPriceTuesday(0.0);// mar
					newRoomPriceListItem.setPriceWednesday(0.0);// mer
					newRoomPriceListItem.setPriceThursday(0.0);// gio
					newRoomPriceListItem.setPriceFriday(0.0);// ven
					newRoomPriceListItem.setPriceSaturday(0.0);// sab
					newRoomPriceListItem.setPriceSunday(0.0);// dom
					roomItems.add(newRoomPriceListItem);
				}
				newRoomPriceList.setItems(roomItems);
				this.getRoomPriceListService().insertRoomPriceList(newRoomPriceList);
				
				
				//ExtraPriceList
				newExtraPriceList = new ExtraPriceList();
				newExtraPriceList.setSeason(theNewSeason);
				newExtraPriceList.setRoomType(eachRoomType);
				newExtraPriceList.setConvention(eachConvention);
				List<ExtraPriceListItem> extraItems = new ArrayList<ExtraPriceListItem>();
				for (Extra eachExtra : this.getExtraService()
						.findExtrasByIdStructure(structure.getId())) {
					newExtraPriceListItem = new ExtraPriceListItem();
					newExtraPriceListItem.setExtra(eachExtra);
					newExtraPriceListItem.setPrice(price);
					extraItems.add(newExtraPriceListItem);
				}
				newExtraPriceList.setItems(extraItems);
				this.getExtraPriceListService().insertExtraPriceList(structure,newExtraPriceList);
			}
		}
	}
	
	public void addPriceListsForRoomType(Structure structure, Integer id_roomType) {
		RoomPriceList newRoomPriceList = null;
		ExtraPriceList newExtraPriceList = null;
		RoomPriceListItem newRoomPriceListItem = null;
		ExtraPriceListItem newExtraPriceListItem = null;
		Double price = 0.0;
		RoomType theNewRoomType = null;

		theNewRoomType = this.getRoomTypeService().findRoomTypeById(id_roomType);
		
		for (Season eachSeason : this.getSeasonService().findSeasonsByIdStructure(structure.getId())) {
			for (Convention eachConvention : this.getConventionService().findConventionsByIdStructure(structure.getId())) {
				//RoomPriceList
				newRoomPriceList = new RoomPriceList();
				newRoomPriceList.setId_structure(structure.getId());
				newRoomPriceList.setSeason(eachSeason);
				newRoomPriceList.setId_season(eachSeason.getId());
				newRoomPriceList.setRoomType(theNewRoomType);
				newRoomPriceList.setId_roomType(theNewRoomType.getId());
				newRoomPriceList.setConvention(eachConvention);
				newRoomPriceList.setId_convention(eachConvention.getId());
				List<RoomPriceListItem> roomItems = new ArrayList<RoomPriceListItem>();
				for (int i = 1; i <= theNewRoomType.getMaxGuests(); i++) {
					newRoomPriceListItem = new RoomPriceListItem();
					newRoomPriceListItem.setNumGuests(i);
					newRoomPriceListItem.setPriceMonday(0.0);// lun
					newRoomPriceListItem.setPriceTuesday(0.0);// mar
					newRoomPriceListItem.setPriceWednesday(0.0);// mer
					newRoomPriceListItem.setPriceThursday(0.0);// gio
					newRoomPriceListItem.setPriceFriday(0.0);// ven
					newRoomPriceListItem.setPriceSaturday(0.0);// sab
					newRoomPriceListItem.setPriceSunday(0.0);// dom
					roomItems.add(newRoomPriceListItem);
				}
				newRoomPriceList.setItems(roomItems);
				this.getRoomPriceListService().insertRoomPriceList(newRoomPriceList);
				
				
				//ExtraPriceList
				newExtraPriceList = new ExtraPriceList();
				newExtraPriceList.setSeason(eachSeason);
				newExtraPriceList.setRoomType(theNewRoomType);
				newExtraPriceList.setConvention(eachConvention);
				List<ExtraPriceListItem> extraItems = new ArrayList<ExtraPriceListItem>();
				for (Extra eachExtra : this.getExtraService()
						.findExtrasByIdStructure(structure.getId())) {
					newExtraPriceListItem = new ExtraPriceListItem();
					newExtraPriceListItem.setExtra(eachExtra);
					newExtraPriceListItem.setPrice(price);
					extraItems.add(newExtraPriceListItem);
				}
				newExtraPriceList.setItems(extraItems);
				this.getExtraPriceListService().insertExtraPriceList(structure,newExtraPriceList);
			}
		}
	}
	
	public void addPriceListsForConvention(Structure structure, Integer id_convention) {
		RoomPriceList newRoomPriceList = null;
		ExtraPriceList newExtraPriceList = null;
		RoomPriceListItem newRoomPriceListItem = null;
		ExtraPriceListItem newExtraPriceListItem = null;
		Double price = 0.0;
		Convention theNewConvention = null;

		theNewConvention = this.getConventionService().findConventionById(id_convention);
		
		for (Season eachSeason : this.getSeasonService().findSeasonsByIdStructure(structure.getId())) {
			for (RoomType eachRoomType : this.getRoomTypeService().findRoomTypesByIdStructure(structure.getId())) {
				//RoomPriceList
				newRoomPriceList = new RoomPriceList();
				newRoomPriceList.setId_structure(structure.getId());
				newRoomPriceList.setSeason(eachSeason);
				newRoomPriceList.setId_season(eachSeason.getId());
				newRoomPriceList.setRoomType(eachRoomType);
				newRoomPriceList.setId_roomType(eachRoomType.getId());
				newRoomPriceList.setConvention(theNewConvention);
				newRoomPriceList.setId_convention(theNewConvention.getId());
				List<RoomPriceListItem> roomItems = new ArrayList<RoomPriceListItem>();
				for (int i = 1; i <= eachRoomType.getMaxGuests(); i++) {
					newRoomPriceListItem = new RoomPriceListItem();
					newRoomPriceListItem.setNumGuests(i);
					newRoomPriceListItem.setPriceMonday(0.0);// lun
					newRoomPriceListItem.setPriceTuesday(0.0);// mar
					newRoomPriceListItem.setPriceWednesday(0.0);// mer
					newRoomPriceListItem.setPriceThursday(0.0);// gio
					newRoomPriceListItem.setPriceFriday(0.0);// ven
					newRoomPriceListItem.setPriceSaturday(0.0);// sab
					newRoomPriceListItem.setPriceSunday(0.0);// dom
					roomItems.add(newRoomPriceListItem);
				}
				newRoomPriceList.setItems(roomItems);
				this.getRoomPriceListService().insertRoomPriceList(newRoomPriceList);
				
				
				//ExtraPriceList
				newExtraPriceList = new ExtraPriceList();
				newExtraPriceList.setSeason(eachSeason);
				newExtraPriceList.setRoomType(eachRoomType);
				newExtraPriceList.setConvention(theNewConvention);
				List<ExtraPriceListItem> extraItems = new ArrayList<ExtraPriceListItem>();
				for (Extra eachExtra : this.getExtraService()
						.findExtrasByIdStructure(structure.getId())) {
					newExtraPriceListItem = new ExtraPriceListItem();
					newExtraPriceListItem.setExtra(eachExtra);
					newExtraPriceListItem.setPrice(price);
					extraItems.add(newExtraPriceListItem);
				}
				newExtraPriceList.setItems(extraItems);
				this.getExtraPriceListService().insertExtraPriceList(structure,newExtraPriceList);
			}
		}
	}
	
	public void refreshPriceLists(Structure structure){
		RoomPriceList newRoomPriceList = null;
		ExtraPriceList newExtraPriceList = null;
		RoomPriceListItem newRoomPriceListItem = null;
		ExtraPriceListItem newExtraPriceListItem = null;
		Double price = 0.0;
		
		
		for (Season eachSeason : this.getSeasonService().findSeasonsByIdStructure(structure.getId())) {
			for (RoomType eachRoomType : this.getRoomTypeService().findRoomTypesByIdStructure(structure.getId())) {
				for (Convention eachConvention : this.getConventionService().findConventionsByIdStructure(structure.getId())) {
					newRoomPriceList = new RoomPriceList();
				  	newRoomPriceList.setId_structure(structure.getId());
					newRoomPriceList.setSeason(eachSeason);
					newRoomPriceList.setId_season(eachSeason.getId());
					newRoomPriceList.setRoomType(eachRoomType);
					newRoomPriceList.setId_roomType(eachRoomType.getId());
					newRoomPriceList.setConvention(eachConvention);
					newRoomPriceList.setId_convention(eachConvention.getId());
					List<RoomPriceListItem> roomItems = new ArrayList<RoomPriceListItem>();
					for (int i=1; i<=eachRoomType.getMaxGuests(); i++) {
						newRoomPriceListItem = new RoomPriceListItem();
					  	newRoomPriceListItem.setNumGuests(i);
						newRoomPriceListItem.setPriceMonday(0.0);// lun
						newRoomPriceListItem.setPriceTuesday(0.0);// mar
						newRoomPriceListItem.setPriceWednesday(0.0);// mer
						newRoomPriceListItem.setPriceThursday(0.0);// gio
						newRoomPriceListItem.setPriceFriday(0.0);// ven
						newRoomPriceListItem.setPriceSaturday(0.0);// sab
						newRoomPriceListItem.setPriceSunday(0.0);// dom
						roomItems.add(newRoomPriceListItem);
					}
					newRoomPriceList.setItems(roomItems);
					this.getRoomPriceListService().insertRoomPriceList(newRoomPriceList);
					newExtraPriceList = new ExtraPriceList();
					newExtraPriceList.setSeason(eachSeason);
					newExtraPriceList.setRoomType(eachRoomType);
					newExtraPriceList.setConvention(eachConvention);
					List<ExtraPriceListItem> extraItems = new ArrayList<ExtraPriceListItem>();
					for (Extra eachExtra : this.getExtraService().findExtrasByIdStructure(structure.getId())) {
						newExtraPriceListItem = new ExtraPriceListItem();
						newExtraPriceListItem.setExtra(eachExtra);
						newExtraPriceListItem.setPrice(price);
						extraItems.add(newExtraPriceListItem);
					}
					newExtraPriceList.setItems(extraItems);
					this.getExtraPriceListService().insertExtraPriceList(structure, newExtraPriceList);
				}
			}
		}
	}

	@Override
	public Boolean hasRoomFreeInPeriod(Structure structure, Integer roomId, Date dateIn, Date dateOut) {
		//Estraggo i Booking della camera con roomId dato
		List<Booking> roomBookings = new ArrayList<Booking>();
		
		for(Booking each: this.getBookingService().findBookingsByIdStructure(structure.getId())){
			if(each.getRoom().getId().equals(roomId)){
				roomBookings.add(each);
			}
		}
		//               dateIn |-------------------------| dateOut              dateIn |-----| dateOut 
		//       |------------------|    |---|     |---------------------------|    |------------------|  roomBookings
		//             aBooking         aBooking         aBooking							aBooking 
		
		for(Booking aBooking: roomBookings){
			if(aBooking.getDateOut().after(dateIn) && (aBooking.getDateOut().compareTo(dateOut)<= 0 ) ){
				return false;
			}
			if(aBooking.getDateIn().after(dateIn) && aBooking.getDateIn().before(dateOut)){
				return false;
			}
			if(aBooking.getDateIn().after(dateIn) && aBooking.getDateOut().before(dateOut)){
				return false;
			}
			if(aBooking.getDateOut().after(dateOut) && aBooking.getDateIn().compareTo(dateIn)<=0){
				return false;
			}
		}
		return true;
	}

	@Override
	public Boolean hasRoomFreeForBooking(Structure structure, Booking booking) {
		//Estraggo i Booking della camera con roomId dato
		List<Booking> roomBookings = new ArrayList<Booking>();
		
		for(Booking each: this.getBookingService().findBookingsByIdStructure(structure.getId())){
			if(each.getRoom().getId().equals(booking.getRoom().getId()) && !each.equals(booking)){
				roomBookings.add(each);
			}
		}
		//              dateIn |--------------------------| dateOut    dateIn |--------| dateOut
		//       |------------------|    |---|     |--------------------------------------|    roomBookings
		//             aBooking         aBooking         aBooking
		
		for(Booking aBooking: roomBookings){
			if(aBooking.getDateOut().after(booking.getDateIn()) && aBooking.getDateIn().before(booking.getDateOut())){
				return false;
			}
			if(aBooking.getDateIn().before(booking.getDateOut()) && aBooking.getDateOut().after(booking.getDateIn())){
				return false;
			}
			if (aBooking.getDateOut().after(booking.getDateOut()) && aBooking.getDateIn().before(booking.getDateIn())) {
				return false;
			}
		}
		return true;	
	}

	@Override
	public Boolean hasPeriodFreeForSeason(Structure structure, List<Period> periods) {
		//Estraggo i Booking della camera con roomId dato
		List<Period> currentPeriods = new ArrayList<Period>();
		Integer currentSeasonId = periods.get(0).getId_season();
		for(Season each: this.getSeasonService().findSeasonsByIdStructure(structure.getId())){
			
			if(! currentSeasonId.equals(each.getId())){
				currentPeriods.addAll(each.getPeriods());
			}
		}
		
		for (Period period : periods) {
			List<Period> siblingPeriods = new ArrayList<Period>();
			siblingPeriods.addAll(periods);
			siblingPeriods.remove(period);
			currentPeriods.addAll(siblingPeriods);
			
			for(Period aPeriod : currentPeriods){
				if(aPeriod.getEndDate().after(period.getStartDate()) && aPeriod.getStartDate().before(period.getEndDate())){
					return false;
				}
				if(aPeriod.getStartDate().before(period.getEndDate()) && aPeriod.getEndDate().after(period.getStartDate())){
					return false;
				}
				if (aPeriod.getEndDate().after(period.getEndDate()) && aPeriod.getStartDate().before(period.getStartDate())) {
					return false;
				}
			}
		}
		//              dateIn |--------------------------| dateOut    dateIn |--------| dateOut
		//       |------------------|    |---|     |--------------------------------------|    roomBookings
		//             aBooking         aBooking         aBooking
		return true;	
	}
	
	
	@Override
	public Boolean hasPeriodFreeForSeason(Structure structure, Season aSeason) {
		//Estraggo i Booking della camera con roomId dato
		List<Period> currentPeriods = new ArrayList<Period>();
		Integer currentSeasonId = aSeason.getId();
		List<Period> periods = aSeason.getPeriods();
		for(Season each: this.getSeasonService().findSeasonsByIdStructure(structure.getId())){
			
			if(currentSeasonId == null || ! currentSeasonId.equals(each.getId())){
				currentPeriods.addAll(each.getPeriods());
			}
		}
		
		for (Period period : periods) {
			List<Period> siblingPeriods = new ArrayList<Period>();
			siblingPeriods.addAll(periods);
			siblingPeriods.remove(period);
			siblingPeriods.addAll(currentPeriods);
			
			
			for(Period aPeriod : siblingPeriods){
				if(aPeriod.getEndDate().after(period.getStartDate()) && aPeriod.getStartDate().before(period.getEndDate())){
					return false;
				}
				if(aPeriod.getStartDate().before(period.getEndDate()) && aPeriod.getEndDate().after(period.getStartDate())){
					return false;
				}
				if (aPeriod.getEndDate().after(period.getEndDate()) && aPeriod.getStartDate().before(period.getStartDate())) {
					return false;
				}
			}
		}
		//              dateIn |--------------------------| dateOut    dateIn |--------| dateOut
		//       |------------------|    |---|     |--------------------------------------|    roomBookings
		//             aBooking         aBooking         aBooking
		return true;	
	}
	
	public SeasonService getSeasonService() {
		return seasonService;
	}


	public void setSeasonService(SeasonService seasonService) {
		this.seasonService = seasonService;
	}


	@Override
	public Integer addRoomFacility(Structure structure, Facility roomFacility) {
		structure.getRoomFacilities().add(roomFacility);
		return 1;
	}


	@Override
	public Facility findRoomFacilityByName(Structure structure,String roomFacilityName) {
		for(Facility each: structure.getRoomFacilities()){
			if(each.getName().equals(roomFacilityName)){
				return each;
			}
		}
		return null;
	}

	
	@Override
	public Facility findRoomFacilityById(Structure structure, Integer id) {
		Facility ret = null;
		
		for (Facility each:structure.getRoomFacilities()){
			if (each.getId().equals(id)) {
				return each;
			}
		}
		return ret;
	}


	@Override
	public List<Facility> findRoomFacilitiesByIds(Structure structure, List<Integer> ids) {
		List<Facility> ret = new ArrayList<Facility>();
		for(Integer each:ids){
			Facility aRoomFacility = this.findRoomFacilityById(structure,each);
			ret.add(aRoomFacility);
		}
		return ret;
	}


	public RoomPriceListService getRoomPriceListService() {
		return roomPriceListService;
	}


	public void setRoomPriceListService(RoomPriceListService roomPriceListService) {
		this.roomPriceListService = roomPriceListService;
	}


	public ExtraPriceListService getExtraPriceListService() {
		return extraPriceListService;
	}


	public void setExtraPriceListService(ExtraPriceListService extraPriceListService) {
		this.extraPriceListService = extraPriceListService;
	}


	public RoomTypeService getRoomTypeService() {
		return roomTypeService;
	}


	public void setRoomTypeService(RoomTypeService roomTypeService) {
		this.roomTypeService = roomTypeService;
	}


	public BookingService getBookingService() {
		return bookingService;
	}


	public void setBookingService(BookingService bookingService) {
		this.bookingService = bookingService;
	}


	public ExtraService getExtraService() {
		return extraService;
	}


	public void setExtraService(ExtraService extraService) {
		this.extraService = extraService;
	}


	public ConventionService getConventionService() {
		return conventionService;
	}


	public void setConventionService(ConventionService conventionService) {
		this.conventionService = conventionService;
	}


	public StructureMapper getStructureMapper() {
		return structureMapper;
	}


	public void setStructureMapper(StructureMapper structureMapper) {
		this.structureMapper = structureMapper;
	}

	
	
	
	public RoomService getRoomService() {
		return roomService;
	}


	public void setRoomService(RoomService roomService) {
		this.roomService = roomService;
	}


	public GuestService getGuestService() {
		return guestService;
	}


	public void setGuestService(GuestService guestService) {
		this.guestService = guestService;
	}
	
	


	public ImageService getImageService() {
		return imageService;
	}


	public void setImageService(ImageService imageService) {
		this.imageService = imageService;
	}
	
	


	public FacilityService getFacilityService() {
		return facilityService;
	}


	public void setFacilityService(
			FacilityService structureFacilityService) {
		this.facilityService = structureFacilityService;
	}

	
	public void buildStructure(Structure structure) {

		//this.buildRoomFacilities(structure);
		//this.buildRoomTypes(structure);
		//this.buildRooms(structure);
		//this.buildGuests(structure);
		//this.buildSeasons(structure);
		//this.buildConventions(structure);
//		this.buildRoomPriceLists(structure);
		//this.buildExtras(structure);
		//this.buildExtraPriceLists(structure);
		//this.buildBookings(structure);
		
	}


	/*
	private void buildRoomFacilities(Structure structure) {
		Facility aRoomFacility = null;

		aRoomFacility = new Facility();
		aRoomFacility.setId(structure.nextKey());
		aRoomFacility.setName("AAD");
		aRoomFacility.setFileName("AAD.gif");
		this.addRoomFacility(structure, aRoomFacility);

		aRoomFacility = new Facility();
		aRoomFacility.setId(structure.nextKey());
		aRoomFacility.setName("BAR");
		aRoomFacility.setFileName("BAR.gif");
		this.addRoomFacility(structure, aRoomFacility);

		aRoomFacility = new Facility();
		aRoomFacility.setId(structure.nextKey());
		aRoomFacility.setName("PHO");
		aRoomFacility.setFileName("PHO.gif");
		this.addRoomFacility(structure, aRoomFacility);

		aRoomFacility = new Facility();
		aRoomFacility.setId(structure.nextKey());
		aRoomFacility.setName("RAD");
		aRoomFacility.setFileName("RAD.gif");
		this.addRoomFacility(structure, aRoomFacility);

		aRoomFacility = new Facility();
		aRoomFacility.setId(structure.nextKey());
		aRoomFacility.setName("TEL");
		aRoomFacility.setFileName("TEL.gif");
		this.addRoomFacility(structure, aRoomFacility);

	}*/
	
	
	private void buildRoomTypes(Structure structure) {
		List<RoomType> roomTypes = null;
		
		roomTypes = this.getRoomTypeService().findRoomTypesByIdStructure(structure.getId());
		structure.setRoomTypes(roomTypes);
	}
	
	
	private void buildRooms(Structure structure) {
		List<Room> rooms = null;
		
		rooms = this.getRoomService().findRoomsByIdStructure(structure.getId());
		structure.setRooms(rooms);
	}

	


	private void buildGuests(Structure structure) {
		structure.setGuests(this.getGuestService().findGuestsByIdStructure(
				structure.getId()));

	}

	/*
	private void buildBookings(Structure structure) {
		Booking aBooking = null;
		Room aRoom = null;
		Guest aGuest = null;
		List<Extra> extras = null;
		Date dateIn = null;
		Date dateOut = null;
		Double roomSubtotal = 0.0;
		Adjustment anAdjustment = null;
		Payment aPayment = null;
		List<ExtraItem> extraItems = null;

		aBooking = new Booking();
		aRoom = this.getRoomService().findRoomByIdStructureAndName(structure.getId(), "101");

		aGuest = this.getGuestService()
				.findGuestsByIdStructure(structure.getId()).get(0);
		extras = new ArrayList<Extra>();
		extras.add(this.getExtraService()
				.findExtrasByIdStructure(structure.getId()).get(0));
		extras.add(this.getExtraService()
				.findExtrasByIdStructure(structure.getId()).get(1));
		aBooking.addExtras(extras);
		aBooking.setBooker(aGuest);
		aBooking.setRoom(aRoom);
		dateIn = new Date(System.currentTimeMillis());
		dateOut = new Date(System.currentTimeMillis() + 3 * 24 * 3600 * 1000);
		dateIn = DateUtils.truncate(dateIn, Calendar.DAY_OF_MONTH);
		dateOut = DateUtils.truncate(dateOut, Calendar.DAY_OF_MONTH);
		aBooking.setDateIn(dateIn);
		aBooking.setDateOut(dateOut);
		aBooking.setId(structure.nextKey());
		aBooking.setNrGuests(1);
		aBooking.setConvention(this.getConventionService()
				.findConventionsByIdStructure(structure.getId()).get(0));
		roomSubtotal = this.getBookingService()
				.calculateRoomSubtotalForBooking(structure, aBooking);

		aBooking.setRoomSubtotal(roomSubtotal);
		extraItems = this.calculateBookedExtraItems(structure, aBooking);
		aBooking.setExtraItems(extraItems);
		aBooking.updateExtraSubtotal();

		anAdjustment = new Adjustment();
		anAdjustment.setId(structure.nextKey());
		anAdjustment.setDate(DateUtils.truncate(new Date(),
				Calendar.DAY_OF_MONTH));
		anAdjustment.setDescription("Sconto per doccia malfunzionante");
		anAdjustment.setAmount(new Double("-50.0"));
		aBooking.addAdjustment(anAdjustment);

		aPayment = new Payment();
		aPayment.setId(structure.nextKey());
		aPayment.setDate(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH));
		aPayment.setDescription("Acconto");
		aPayment.setAmount(new Double("60.0"));
		aBooking.addPayment(aPayment);
		aBooking.setStatus("checkedout");
		this.getBookingService().insertBooking(structure, aBooking);
	}*/

	/*
	private List<ExtraItem> calculateBookedExtraItems(
			Structure structure, Booking booking) {
		ExtraItem extraItem = null;
		List<ExtraItem> extraItems = null;

		extraItems = new ArrayList<ExtraItem>();
		for (Extra each : booking.getExtras()) {
			extraItem = booking.findExtraItem(each);
			if (extraItem == null) {
				extraItem = new ExtraItem();
				extraItem.setExtra(each);
				extraItem.setQuantity(booking
						.calculateExtraItemMaxQuantity(each));
				extraItem.setMaxQuantity(booking
						.calculateExtraItemMaxQuantity(each));
				extraItem.setUnitaryPrice(this.calculateExtraItemUnitaryPrice(structure,
								booking.getDateIn(), booking.getDateOut(),
								booking.getRoom().getRoomType(),
								booking.getConvention(), each));

			} else {
				extraItem.setMaxQuantity(booking
						.calculateExtraItemMaxQuantity(each));
				extraItem.setUnitaryPrice(this.calculateExtraItemUnitaryPrice(structure,
								booking.getDateIn(), booking.getDateOut(),
								booking.getRoom().getRoomType(),
								booking.getConvention(), each));
			}
			extraItems.add(extraItem);
		}
		return extraItems;
	}*/

	private void buildExtras(Structure structure) {

		structure.setExtras(this.getExtraService().findExtrasByIdStructure(
				structure.getId()));

	}

	private void buildSeasons(Structure structure) {
		Season aSeason = null;
		Period aPeriod = null;
		SimpleDateFormat sdf = null;

		structure.setSeasons(this.getSeasonService().findSeasonsByIdStructure(
				structure.getId()));

	}

	/*
	private void buildConventions(Structure structure) {
		Convention convention = null;

		// convenzione di default
		convention = new Convention();
		convention.setId(structure.nextKey());
		convention.setName("agevolazione Default");
		convention.setDescription("Default convention");
		convention.setActivationCode("XXX");

		convention.setId_structure(structure.getId());
		this.getConventionService().insertConvention(convention);

	}*/

	private void buildExtraPriceLists(Structure structure) {
		ExtraPriceList extraPriceList = null;
		ExtraPriceListItem extraPriceListItem = null;
		Double price = null;

		// Listino Extra per Camera Singola Bassa Stagione
		extraPriceList = new ExtraPriceList();
		extraPriceList.setId(structure.nextKey());
		extraPriceList.setRoomType(this.getRoomTypeService()
				.findRoomTypesByIdStructure(structure.getId()).get(0));
		extraPriceList.setSeason(this.getSeasonService().findSeasonByName(
				structure.getId(), "Bassa Stagione"));
		extraPriceList.setConvention(this.getConventionService()
				.findConventionsByIdStructure(structure.getId()).get(0));
		for (Extra eachExtra : this.getExtraService().findExtrasByIdStructure(
				structure.getId())) {
			extraPriceListItem = new ExtraPriceListItem();
			extraPriceListItem.setId(structure.nextKey());
			extraPriceListItem.setExtra(eachExtra);
			price = 10.0;
			extraPriceListItem.setPrice(price);
			extraPriceList.addItem(extraPriceListItem);
		}
		this.getExtraPriceListService().insertExtraPriceList(structure,
				extraPriceList);

		// Listino Extra per Camera Singola Alta Stagione
		extraPriceList = new ExtraPriceList();
		extraPriceList.setId(structure.nextKey());
		extraPriceList.setRoomType(this.getRoomTypeService()
				.findRoomTypesByIdStructure(structure.getId()).get(0));
		extraPriceList.setSeason(this.getSeasonService().findSeasonByName(
				structure.getId(), "Alta Stagione"));
		extraPriceList.setConvention(this.getConventionService()
				.findConventionsByIdStructure(structure.getId()).get(0));
		for (Extra eachExtra : this.getExtraService().findExtrasByIdStructure(
				structure.getId())) {
			extraPriceListItem = new ExtraPriceListItem();
			extraPriceListItem.setId(structure.nextKey());
			extraPriceListItem.setExtra(eachExtra);
			price = 15.0;
			extraPriceListItem.setPrice(price);
			extraPriceList.addItem(extraPriceListItem);
		}
		this.getExtraPriceListService().insertExtraPriceList(structure,
				extraPriceList);
		// Listino Extra per Camera Doppia Bassa Stagione
		extraPriceList = new ExtraPriceList();
		extraPriceList.setId(structure.nextKey());
		extraPriceList.setRoomType(structure.getRoomTypes().get(1));
		extraPriceList.setSeason(this.getSeasonService().findSeasonByName(
				structure.getId(), "Bassa Stagione"));
		extraPriceList.setConvention(this.getConventionService()
				.findConventionsByIdStructure(structure.getId()).get(0));
		for (Extra eachExtra : this.getExtraService().findExtrasByIdStructure(
				structure.getId())) {
			extraPriceListItem = new ExtraPriceListItem();
			extraPriceListItem.setId(structure.nextKey());
			extraPriceListItem.setExtra(eachExtra);
			price = 10.0;
			extraPriceListItem.setPrice(price);
			extraPriceList.addItem(extraPriceListItem);
		}
		this.getExtraPriceListService().insertExtraPriceList(structure,
				extraPriceList);

		// Listino Extra per Camera Doppia Alta Stagione
		extraPriceList = new ExtraPriceList();
		extraPriceList.setId(structure.nextKey());
		extraPriceList.setRoomType(structure.getRoomTypes().get(1));
		extraPriceList.setSeason(this.getSeasonService().findSeasonByName(
				structure.getId(), "Alta Stagione"));
		extraPriceList.setConvention(this.getConventionService()
				.findConventionsByIdStructure(structure.getId()).get(0));
		for (Extra eachExtra : this.getExtraService().findExtrasByIdStructure(
				structure.getId())) {
			extraPriceListItem = new ExtraPriceListItem();
			extraPriceListItem.setId(structure.nextKey());
			extraPriceListItem.setExtra(eachExtra);
			price = 15.0;
			extraPriceListItem.setPrice(price);
			extraPriceList.addItem(extraPriceListItem);
		}
		this.getExtraPriceListService().insertExtraPriceList(structure,
				extraPriceList);
	}

	/*private void buildRoomPriceLists(Structure structure) {
		RoomPriceList roomPriceList = null;
		RoomPriceListItem roomPriceListItem = null;
		Double[] prices = null;

		// Listino Room per Camera Singola Bassa Stagione
		roomPriceList = new RoomPriceList();
		roomPriceList.setId(structure.nextKey());
		roomPriceList.setRoomType(this.getRoomTypeService()
				.findRoomTypesByIdStructure(structure.getId()).get(0));
		roomPriceList.setSeason(this.getSeasonService().findSeasonByName(
				structure.getId(), "Bassa Stagione"));
		roomPriceList.setConvention(this.getConventionService()
				.findConventionsByIdStructure(structure.getId()).get(0));
		roomPriceListItem = new RoomPriceListItem();
		roomPriceListItem.setId(structure.nextKey());
		roomPriceListItem.setNumGuests(1);
		
		roomPriceListItem.setPriceMonday(50.0);// lun
		roomPriceListItem.setPriceTuesday(50.0);// mar
		roomPriceListItem.setPriceWednesday(50.0);// mer
		roomPriceListItem.setPriceThursday(50.0);// gio
		roomPriceListItem.setPriceFriday(50.0);// ven
		roomPriceListItem.setPriceSaturday(50.0);// sab
		roomPriceListItem.setPriceSunday(50.0);// dom
		
		roomPriceList.addItem(roomPriceListItem);
		this.getRoomPriceListService().insertRoomPriceList(structure,
				roomPriceList);

		// Listino Room per Camera Singola Alta Stagione
		roomPriceList = new RoomPriceList();
		roomPriceList.setId(structure.nextKey());
		roomPriceList.setRoomType(this.getRoomTypeService()
				.findRoomTypesByIdStructure(structure.getId()).get(0));
		roomPriceList.setSeason(this.getSeasonService().findSeasonByName(
				structure.getId(), "Alta Stagione"));
		roomPriceList.setConvention(this.getConventionService()
				.findConventionsByIdStructure(structure.getId()).get(0));
		roomPriceListItem = new RoomPriceListItem();
		roomPriceListItem.setId(structure.nextKey());
		roomPriceListItem.setNumGuests(1);
		roomPriceListItem.setPriceMonday(80.0);// lun
		roomPriceListItem.setPriceTuesday(80.0);// mar
		roomPriceListItem.setPriceWednesday(80.0);// mer
		roomPriceListItem.setPriceThursday(80.0);// gio
		roomPriceListItem.setPriceFriday(80.0);// ven
		roomPriceListItem.setPriceSaturday(80.0);// sab
		roomPriceListItem.setPriceSunday(80.0);// dom
		roomPriceList.addItem(roomPriceListItem);
		this.getRoomPriceListService().insertRoomPriceList(structure,
				roomPriceList);

		// Listino Room per Camera Doppia Bassa Stagione
		roomPriceList = new RoomPriceList();
		roomPriceList.setId(structure.nextKey());
		roomPriceList.setRoomType(this.getRoomTypeService()
				.findRoomTypesByIdStructure(structure.getId()).get(1));
		roomPriceList.setSeason(this.getSeasonService().findSeasonByName(
				structure.getId(), "Bassa Stagione"));
		roomPriceList.setConvention(this.getConventionService()
				.findConventionsByIdStructure(structure.getId()).get(0));
		roomPriceListItem = new RoomPriceListItem();
		roomPriceListItem.setId(structure.nextKey());
		roomPriceListItem.setNumGuests(1);
		roomPriceListItem.setPriceMonday(80.0);// lun
		roomPriceListItem.setPriceTuesday(80.0);// mar
		roomPriceListItem.setPriceWednesday(80.0);// mer
		roomPriceListItem.setPriceThursday(80.0);// gio
		roomPriceListItem.setPriceFriday(80.0);// ven
		roomPriceListItem.setPriceSaturday(80.0);// sab
		roomPriceListItem.setPriceSunday(80.0);// dom
		roomPriceList.addItem(roomPriceListItem);

		roomPriceListItem = new RoomPriceListItem();
		roomPriceListItem.setId(structure.nextKey());
		roomPriceListItem.setNumGuests(2);
		roomPriceListItem.setPriceMonday(100.0);// lun
		roomPriceListItem.setPriceTuesday(100.0);// mar
		roomPriceListItem.setPriceWednesday(100.0);// mer
		roomPriceListItem.setPriceThursday(100.0);// gio
		roomPriceListItem.setPriceFriday(100.0);// ven
		roomPriceListItem.setPriceSaturday(100.0);// sab
		roomPriceListItem.setPriceSunday(100.0);// dom
		roomPriceList.addItem(roomPriceListItem);

		// structure.addRoomPriceList(roomPriceList);
		this.getRoomPriceListService().insertRoomPriceList(structure,
				roomPriceList);

		// Listino Room per Camera Doppia Alta Stagione
		roomPriceList = new RoomPriceList();
		roomPriceList.setId(structure.nextKey());
		roomPriceList.setRoomType(this.getRoomTypeService()
				.findRoomTypesByIdStructure(structure.getId()).get(1));
		roomPriceList.setSeason(this.getSeasonService().findSeasonByName(
				structure.getId(), "Alta Stagione"));
		roomPriceList.setConvention(this.getConventionService()
				.findConventionsByIdStructure(structure.getId()).get(0));
		roomPriceListItem = new RoomPriceListItem();
		roomPriceListItem.setId(structure.nextKey());
		roomPriceListItem.setNumGuests(1);
		roomPriceListItem.setPriceMonday(90.0);// lun
		roomPriceListItem.setPriceTuesday(90.0);// mar
		roomPriceListItem.setPriceWednesday(90.0);// mer
		roomPriceListItem.setPriceThursday(90.0);// gio
		roomPriceListItem.setPriceFriday(90.0);// ven
		roomPriceListItem.setPriceSaturday(90.0);// sab
		roomPriceListItem.setPriceSunday(90.0);// dom
		roomPriceList.addItem(roomPriceListItem);

		roomPriceListItem = new RoomPriceListItem();
		roomPriceListItem.setId(structure.nextKey());
		roomPriceListItem.setNumGuests(2);
		roomPriceListItem.setPriceMonday(130.0);// lun
		roomPriceListItem.setPriceTuesday(130.0);// mar
		roomPriceListItem.setPriceWednesday(130.0);// mer
		roomPriceListItem.setPriceThursday(130.0);// gio
		roomPriceListItem.setPriceFriday(130.0);// ven
		roomPriceListItem.setPriceSaturday(130.0);// sab
		roomPriceListItem.setPriceSunday(130.0);// dom
		roomPriceList.addItem(roomPriceListItem);

		this.getRoomPriceListService().insertRoomPriceList(structure,
				roomPriceList);
	}*/

	

	
	

}
