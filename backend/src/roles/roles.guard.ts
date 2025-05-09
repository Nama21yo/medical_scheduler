// File: src/common/guards/roles.guard.ts
import {
  CanActivate,
  ExecutionContext,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { JwtService } from '@nestjs/jwt';

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(
    private reflector: Reflector,
    private readonly jwtService: JwtService,
  ) {}

  canActivate(context: ExecutionContext): boolean {
    const roles = this.reflector.get<string[]>('roles', context.getHandler());
    if (!roles) {
      return true; // No roles specified, allow access
    }

    const request = context.switchToHttp().getRequest();
    const authHeader = request.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new UnauthorizedException(
        'Authorization token not provided or invalid',
      );
    }

    const jwt = authHeader.replace('Bearer ', '');
    const decoded = this.jwtService.decode(jwt);

    if (!decoded || !decoded.role || !decoded.role.name) {
      throw new UnauthorizedException('Invalid or malformed token');
    }

    const userRole = decoded.role.name.toLowerCase();
    const allowedRoles = roles.map((role) => role.toLowerCase());

    return allowedRoles.includes(userRole);
  }
}
