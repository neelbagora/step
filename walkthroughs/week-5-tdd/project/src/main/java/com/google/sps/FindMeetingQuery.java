// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

public final class FindMeetingQuery {
<<<<<<< HEAD
  
  /**
   * query takes in events and a request and determines the best possible TimeRange for the meeting
   * to take place. If no time is available, returns TimeRange([]). TimeRange is represented in minutes
   * so the day starts at 0 minutes and ends at 1440 minutes.
   *
   * @param  events  The events that are already in the schedule that potentially make a time unavailable.
   * @param  request The meeting request being made.
   * @return The Collection of potential TimeRange objects.
   */
=======
  ArrayList<Event> optionalEvents = null;
>>>>>>> parent of 54bae8b... Clean code up.
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    long timeNeeded = request.getDuration();
    if (timeNeeded > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }    
    if (events.size() == 0) {
      return Arrays.asList(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true));
    }

    // Build collection of occupied zones.
    Collection<TimeRange> occupiedTimes = getOccupiedTimeZones(events, request);
    
    ArrayList<TimeRange> validTimes = new ArrayList<>();
    int freeStart = TimeRange.START_OF_DAY;
    int freeEnd = TimeRange.END_OF_DAY;
    ArrayList<TimeRange> overlapRanges = new ArrayList<>();
  
    // Form available TimeRanges
    for (TimeRange timeRange : occupiedTimes) {
      freeEnd = timeRange.start();
      if (freeStart == freeEnd) {
        freeStart = timeRange.end();
      }
      else {
        if (freeEnd - freeStart >= timeNeeded) {
          validTimes.add(TimeRange.fromStartEnd(freeStart, freeEnd - 1, true));
          boolean overlapped = false;
          for (Event event : optionalEvents) {
            // Keeping track of events overlapping optional events
            if (event.getWhen().overlaps(TimeRange.fromStartEnd(freeStart, freeEnd - 1, true))) {
              overlapRanges.add(TimeRange.fromStartEnd(freeStart, freeEnd - 1, true));
              break;
            }
          }
        }
        freeStart = timeRange.end();
      }
    }
    if ((freeStart != TimeRange.END_OF_DAY + 1) && ((TimeRange.END_OF_DAY + 1 - freeStart) >= timeNeeded)) {
      validTimes.add(TimeRange.fromStartEnd(freeStart, TimeRange.END_OF_DAY, true));
    }

    // There are no free zones where everyone can attend.
    if (overlapRanges.size() == validTimes.size()) {
      return validTimes;
    }
    else {
      // Remove the events in which optional people cannot go to.
      for (TimeRange range : overlapRanges) {
        validTimes.remove(range);
      }
      return validTimes;
    }
  }

  /**
   * getOccupiedTimeZones takes in the existing events and a meeting request and returns a list of
   * optimized TimeRange('s) that are in the timeline. Optimized in this context means that events
   * that are overlapping are combined to make it easier to create meeting slots without the need
   * to read every single (sometimes redundant) event blocks.
   *
   * @param  events  The events that are already in the schedule that potentially make a time unavailable.
   * @param  request The meeting request being made.
   * @return The Collection of optimized TimeRange objects.
   */
  public Collection<TimeRange> getOccupiedTimeZones(Collection<Event> events, MeetingRequest request) {
    Iterator<Event> iterator = events.iterator();
    ArrayList<TimeRange> occupiedTimes = new ArrayList<>();
    ArrayList<Event> optionalEventsTemp = new ArrayList<>();

    if (request.getAttendees().isEmpty()) {
      request = new MeetingRequest(request.getOptionalAttendees(), request.getDuration());
    }
    // This while loop iterates over all the events that are passed in and determines
    // if there are any conflicts with mandatory attendees.
    ArrayList<String> attendees = new ArrayList<>(request.getAttendees());
    while (iterator.hasNext()) {
      Event currentEvent = iterator.next();
      System.out.println(currentEvent.getTitle());
      System.out.println(currentEvent.getWhen().toString());
      boolean conflict = false;
<<<<<<< HEAD
      
      //locate potential conflicts with required attendees.
      if (currentEvent.getAttendees().stream().anyMatch(attendee -> attendees.contains(attendee))) {
=======
      ArrayList<String> attendees = new ArrayList<>(request.getAttendees());
      ArrayList<String> optionalAttendees = new ArrayList<>(request.getOptionalAttendees());

      //locate potential conflicts with required attendees.
      for (int i = 0; i < attendees.size(); i++) {
        if (currentEvent.getAttendees().contains(attendees.get(i))) {
          System.out.println("Conflict");
          conflict = true;
          break;
        }
      }

      if (conflict) {
>>>>>>> parent of 54bae8b... Clean code up.
        occupiedTimes.add(currentEvent.getWhen());
      }
      else {
        //No conflict -> locate potential optional conflicts.
        for (int i = 0; i < optionalAttendees.size(); i++) {
          if (currentEvent.getAttendees().contains(optionalAttendees.get(i))) {
            optionalEventsTemp.add(currentEvent);
          }
        }
      }
    }
    Collections.sort(occupiedTimes, TimeRange.ORDER_BY_START);
    Collections.sort(optionalEventsTemp, Event.ORDER_BY_START);
    optionalEvents = optionalEventsTemp;

    TimeRange previousTimeRange = null;
    ArrayList<TimeRange> newTimes = new ArrayList<>();
    
    //merge overlapping TimeRange('s)
    // Ex:          |----A----|
    //                     |--A--|
    //-------------------------------
    // Result:      |-----A------|
    for (TimeRange time : occupiedTimes) {
      if (previousTimeRange == null) {
        previousTimeRange = time;
      }
      else if ((time.start() > previousTimeRange.start()) && (time.start() < previousTimeRange.end())) {
        if (time.end() >= previousTimeRange.end()) {
          previousTimeRange = TimeRange.fromStartEnd(previousTimeRange.start(), time.end(), false);
        }
      }
      else {
        newTimes.add(previousTimeRange);
        previousTimeRange = time;
      }
    }
    if (previousTimeRange != null) {
      newTimes.add(previousTimeRange);
    }
    return newTimes;
  }
}
