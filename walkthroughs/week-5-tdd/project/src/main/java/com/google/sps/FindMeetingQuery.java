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
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    long timeNeeded = request.getDuration();
    if (timeNeeded > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }    
    if (events.size() == 0) {
      return Arrays.asList(TimeRange.fromStartEnd(TimeRange.START_OF_DAY, TimeRange.END_OF_DAY, true));
    }

    Collection<TimeRange> occupiedTimes = getOccupiedTimeZones(events, request);
    
    ArrayList<TimeRange> validTimes = new ArrayList<>();
    int freeStart = TimeRange.START_OF_DAY;
    int freeEnd = TimeRange.END_OF_DAY;
    System.out.println("Start run");
    for (TimeRange timeRange : occupiedTimes) {
      freeEnd = timeRange.start();
      if (freeStart == freeEnd) {
        freeStart = timeRange.end();
      }
      else {
        if (freeEnd - freeStart >= timeNeeded) {
          validTimes.add(TimeRange.fromStartEnd(freeStart, freeEnd - 1, true));
        }
        freeStart = timeRange.end();
      }
    }
    if ((freeStart != TimeRange.END_OF_DAY + 1) && ((TimeRange.END_OF_DAY + 1 - freeStart) >= timeNeeded)) {
      validTimes.add(TimeRange.fromStartEnd(freeStart, TimeRange.END_OF_DAY, true));
    }
    return validTimes;
  }

  public Collection<TimeRange> getOccupiedTimeZones(Collection<Event> events, MeetingRequest request) {
    Iterator<Event> iterator = events.iterator();
    ArrayList<TimeRange> occupiedTimes = new ArrayList<>();
    System.out.println("ADd Times");
    while (iterator.hasNext()) {
      Event currentEvent = iterator.next();
      System.out.println(currentEvent.getTitle());
      System.out.println(currentEvent.getWhen().toString());
      boolean conflict = false;
      ArrayList<String> attendees = new ArrayList<>(request.getAttendees());
      for (int i = 0; i < attendees.size(); i++) {
        if (currentEvent.getAttendees().contains(attendees.get(i))) {
          System.out.println("Conflict");
          conflict = true;
          break;
        }
      }
      if (conflict) {
        occupiedTimes.add(currentEvent.getWhen());
      }
    }
    Collections.sort(occupiedTimes, TimeRange.ORDER_BY_START);

    TimeRange previousTimeRange = null;
    ArrayList<TimeRange> newTimes = new ArrayList<>();
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
