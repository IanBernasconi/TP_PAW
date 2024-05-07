import { Injectable } from "@angular/core";

export interface ToastInfo {
  body: string;
  header: string;
  classname: string;
  delay?: number;
}

@Injectable({
  providedIn: "root",
})
export class ToastService {
  toasts: ToastInfo[] = [];

  constructor() {}

  show(body: string, classname?: string, header?: string, delay?: number) {
    this.toasts.push({
      body: body,
      header: header || "PartyPicker",
      classname: classname || "",
      delay: delay || 3000,
    });
  }

  success(body: string, header?: string, delay?: number) {
    this.show(body, "bg-success text-light", header, delay);
  }

  warn(body: string, header?: string, delay?: number) {
    this.show(body, "bg-warning text-dark", header, delay);
  }

  error(body: string, header?: string, delay?: number) {
    this.show(body, "bg-danger text-light", header, delay);
  }

  remove(toast: ToastInfo) {
    this.toasts = this.toasts.filter((t) => t !== toast);
  }

  clear() {
    this.toasts.splice(0, this.toasts.length);
  }
}

export const ToastMessages = {
  event: {
    create: {
      success: $localize`Event created successfully`,
      error: $localize`Error creating event`,
    },
    delete: {
      success: $localize`Event deleted successfully`,
      error: $localize`Error deleting event`,
    },
    update: {
      success: $localize`Event updated successfully`,
      error: $localize`Error updating event`,
    },
  },
  relation: {
    create: {
      success: $localize`Service added to event successfully`,
      error: $localize`Error adding service to event`,
    },
    delete: {
      success: $localize`Service removed from event successfully`,
      error: $localize`Error removing service from event`,
    },
    update: {
      success: $localize`Relation updated successfully`,
      error: $localize`Error updating relation`,
    },
    contact: {
      success: $localize`Provider contacted successfully`,
      error: $localize`Error contacting provider`,
    },
  },
  offering: {
    create: {
      success: $localize`Service created successfully`,
      error: $localize`Error creating service`,
    },
    delete: {
      success: $localize`Service deleted successfully`,
      error: $localize`Error deleting service`,
    },
    update: {
      success: $localize`Service updated successfully`,
      error: $localize`Error updating service`,
    },
    changeEvents: {
      success: $localize`Events updated successfully`,
      error: $localize`Error updating events`,
    },
  },
  review: {
    create: {
      success: $localize`Review created successfully`,
      error: $localize`Error creating review`,
    },
    delete: {
      success: $localize`Review deleted successfully`,
      error: $localize`Error deleting review`,
    },
    update: {
      success: $localize`Review updated successfully`,
      error: $localize`Error updating review`,
    },
  },
  guest: {
    add: {
      success: $localize`Guest added successfully`,
      error: $localize`Error adding guest`,
    },
    delete: {
      success: $localize`Guest deleted successfully`,
      error: $localize`Error deleting guest`,
    },
    invite: {
      success: $localize`Guests invited successfully`,
      error: $localize`Error inviting guests`,
    },
    accept: {
      success: $localize`Invitation accepted successfully`,
      error: $localize`Error accepting invitation`,
    },
    reject: {
      success: $localize`Invitation rejected successfully`,
      error: $localize`Error rejecting invitation`,
    },
    alreadyAnswered: $localize`You have already answered this invitation`,
  },
  user: {
    create: {
      success: $localize`User created successfully`,
      error: $localize`Error creating user`,
    },
    delete: {
      success: $localize`User deleted successfully`,
      error: $localize`Error deleting user`,
    },
    update: {
      success: $localize`User updated successfully`,
      error: $localize`Error updating user`,
    },
    becomeProvider: {
      success: $localize`You are now a provider`,
      error: $localize`Error becoming provider`,
    },
  },
  login: {
    success: $localize`Login successful`,
    error: $localize`Error logging in`,
  },
  logout: {
    success: $localize`Logout successful`,
    error: $localize`Error logging out`,
  },
  register: {
    success: $localize`Registration successful`,
    error: $localize`Error registering`,
    verify: {
      success: $localize`Verification successful`,
      error: $localize`Error verifying`,
    },
  },
  token: {
    error: $localize`Invalid token`,
  },
  password: {
    reset: {
      success: $localize`Password reset successful`,
      error: $localize`Error resetting password`,
    },
  },
  email: {
    send: {
      success: $localize`Email sent successfully`,
      error: $localize`Error sending email`,
    },
  },
  profile: {
    update: {
      success: $localize`Profile updated successfully`,
      error: $localize`Error updating profile`,
    },
  },
  access: {
    error: $localize`You do not have access to this resource`,
    forbidden: $localize`You do not have permission to access this resource`,
    loginNeeded: $localize`You must be logged in to access this resource`,
    noLoginNeeded: $localize`You must not be logged in to access this resource`,
    loginFailed: $localize`Login failed`,
    sessionExpired: $localize`Your session has expired`,
    providerStatusNeeded: $localize`You must be a provider to access this resource`,
  },
};
