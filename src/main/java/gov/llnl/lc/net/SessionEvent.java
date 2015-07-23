/************************************************************
 * Copyright (c) 2015, Lawrence Livermore National Security, LLC.
 * Produced at the Lawrence Livermore National Laboratory.
 * Written by Timothy Meier, meier3@llnl.gov, All rights reserved.
 * LLNL-CODE-673346
 *
 * This file is part of the OpenSM Monitoring Service (OMS) package.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License (as published by
 * the Free Software Foundation) version 2.1 dated February 1999.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * OUR NOTICE AND TERMS AND CONDITIONS OF THE GNU GENERAL PUBLIC LICENSE
 *
 * Our Preamble Notice
 *
 * A. This notice is required to be provided under our contract with the U.S.
 * Department of Energy (DOE). This work was produced at the Lawrence Livermore
 * National Laboratory under Contract No.  DE-AC52-07NA27344 with the DOE.
 *
 * B. Neither the United States Government nor Lawrence Livermore National
 * Security, LLC nor any of their employees, makes any warranty, express or
 * implied, or assumes any liability or responsibility for the accuracy,
 * completeness, or usefulness of any information, apparatus, product, or
 * process disclosed, or represents that its use would not infringe privately-
 * owned rights.
 *
 * C. Also, reference herein to any specific commercial products, process, or
 * services by trade name, trademark, manufacturer or otherwise does not
 * necessarily constitute or imply its endorsement, recommendation, or favoring
 * by the United States Government or Lawrence Livermore National Security,
 * LLC. The views and opinions of authors expressed herein do not necessarily
 * state or reflect those of the United States Government or Lawrence Livermore
 * National Security, LLC, and shall not be used for advertising or product
 * endorsement purposes.
 *
 *        file: SessionEvent.java
 *
 *  Created on: Oct 19, 2011
 *      Author: meier3
 ********************************************************************/
package gov.llnl.lc.net;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**********************************************************************
 * Describe purpose and responsibility of SessionEvent
 * <p>
 * @see  related classes and interfaces
 *
 * @author meier3
 * 
 * @version Oct 19, 2011 9:45:06 AM
 **********************************************************************/
public enum SessionEvent
{
  SESSION_EVENT_ADD(       0, "add"),    
  SESSION_EVENT_REMOVE(    1, "remove"),    
  SESSION_EVENT_KILL(      2, "kill"),    
  SESSION_EVENT_ERROR(     3, "error"),    
  SESSION_EVENT_MAX(       4, "final event");
  
  public static final EnumSet<SessionEvent> SESSION_ALL_EVENTS = EnumSet.allOf(SessionEvent.class);
  
  private static final Map<Integer,SessionEvent> lookup = new HashMap<Integer,SessionEvent>();

  static 
  {
    for(SessionEvent s : SESSION_ALL_EVENTS)
         lookup.put(s.getEvent(), s);
  }

  private int Event;
  private String EventName;

private SessionEvent(int Event, String Name)
{
    this.Event = Event;
    this.EventName = Name;
}

public int getEvent()
{
  return Event;
  }

public String getEventName()
{
  return EventName;
  }

public static SessionEvent get(int Event)
{ 
    return lookup.get(Event); 
}


}
