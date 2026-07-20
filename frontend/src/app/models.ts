export interface Item { id: string; name: string; description?: string; url?: string; }
export interface OwnerWishlist { ownerName: string; shareToken: string; items: Item[]; }
export interface GuestItem extends Item { reserved: boolean; reservedBy?: string; note?: string; }
export interface GuestWishlist { ownerName: string; items: GuestItem[]; }
